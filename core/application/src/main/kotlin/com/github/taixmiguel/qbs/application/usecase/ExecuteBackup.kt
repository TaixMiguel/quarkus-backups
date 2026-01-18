package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupCompressor
import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import com.github.taixmiguel.qbs.domain.BackupId

class ExecuteBackup(
    private val backupRepository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry,
    private val compressor: BackupCompressor
) {
    suspend fun execute(backupId: BackupId) {
        backupRepository.findById(backupId)?.let { backup ->
            val storageRepo = ssRegistry.getRepository(backup.storageService)
                ?: throw IllegalArgumentException("Storage service '${backup.storageService}' not found")

            val backupFile = compressor.compress(backup.sourceDir)
            try {
                storageRepo.push(backup.destinationDir, backupFile)
            } finally {
                if (backupFile.exists()) backupFile.delete()
            }
        } ?: throw NoSuchElementException("Backup with id $backupId does not exist")
    }
}
