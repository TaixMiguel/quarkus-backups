package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.filesystem.BackupCompressor
import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.application.port.publisher.MessagePublisher
import com.github.taixmiguel.qbs.application.port.storage.StorageRepository
import com.github.taixmiguel.qbs.application.port.storage.StorageServiceRegistry
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.BackupInstance
import com.github.taixmiguel.qbs.domain.BackupState
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class ExecuteBackup(
    private val backupRepository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry,
    private val compressor: BackupCompressor,
    private val msgPublisher: MessagePublisher
) {
    suspend fun execute(backupId: BackupId) {
        backupRepository.findById(backupId)?.let { backup ->
            val storageRepo = ssRegistry.getRepository(backup.storageService)
                ?: throw IllegalArgumentException("Storage service '${backup.storageService}' not found")

            val bckInstance = BackupInstance.create(backup = backup)
            var bckFile: File? = null
            backup.add(bckInstance)

            try {
                bckFile = compress(bckInstance)
                upload(bckInstance, bckFile, storageRepo)
            } catch (e: Exception) {
                bckInstance.state = BackupState.ERROR
                e.printStackTrace()
            } finally {
                publishBackupStatus(bckInstance, msgPublisher)
                backupRepository.save(bckInstance)
                removeOlderInstances(backup)
                bckFile?.delete()
            }
        } ?: throw NoSuchElementException("Backup with id $backupId does not exist")
    }

    private fun compress(bckInstance: BackupInstance): File {
        val started = Instant.now()
        val backupFile = compressor.compress(bckInstance.backup.sourceDir)
        val ended = Instant.now()

        bckInstance.duration = Duration.between(started, ended).seconds
        bckInstance.state = BackupState.CREATED
        bckInstance.size = backupFile.length()
        return backupFile
    }

    private suspend fun upload(bckInstance: BackupInstance, bckFile: File, storageRepo: StorageRepository) {
        storageRepo.push(bckInstance.backup.destinationDir.toPath(), bckFile)
        bckInstance.state = BackupState.UPLOAD
    }

    private fun publishBackupStatus(bckInstance: BackupInstance, msgPublisher: MessagePublisher) {
        val backup = bckInstance.backup
        val createdAt = bckInstance.createdAt

        try {
            msgPublisher.publish("stat/taixBackupsService/lastBackup", backup.name.value, true)
            msgPublisher.publish("stat/taixBackupsService/lastExecution", "${createdAt.toEpochSecond(ZoneOffset.UTC)}", true)

            if (backup.swSensorMQTT) {
                msgPublisher.publish("stat/taixBackupsService/${backup.id.value}/stateBackup", bckInstance.state.name, true)
                msgPublisher.publish("stat/taixBackupsService/${backup.id.value}/lastExecution", "${createdAt.toEpochSecond(ZoneOffset.UTC)}", true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeOlderInstances(backup: Backup) {
        try {
            while (backup.instances.size > backup.nBackupsMax) {
                val bckInstance = backup.instances.first()
                backupRepository.delete(bckInstance)
                backup.remove(bckInstance)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
