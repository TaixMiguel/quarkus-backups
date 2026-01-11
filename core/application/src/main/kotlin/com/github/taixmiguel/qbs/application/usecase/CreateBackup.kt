package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import java.nio.file.Path

class CreateBackup(
    private val idGenerator: BackupIdGenerator,
    private val repository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry
) {
    fun execute(command: CreateBackupCommand): BackupId {
        if (!ssRegistry.isSupported(command.storageService))
            throw IllegalArgumentException("Storage service '${command.storageService}' not supported. Supported " +
                    "services are ${ssRegistry.supportedServices().joinToString(", ")}")

        val backup = Backup(
            id = idGenerator.generate(),
            name = command.name,
            description = command.description,
            storageService = command.storageService,
            sourceDir = command.sourceDir,
            destinationDir = command.destinationDir,
            username = command.username,
            password = command.password,
            nBackupsMax = command.nBackupsMax,
            swSensorMQTT = command.swSensorMQTT
        )

        repository.save(backup)
        return backup.id
    }
}

data class CreateBackupCommand(
    val name: String, val description: String, val storageService: String,
    val sourceDir: Path, val destinationDir: Path, val username: String? = null,
    val password: String? = null, val nBackupsMax: Int = 15,
    val swSensorMQTT: Boolean = false
)