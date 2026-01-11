package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import com.github.taixmiguel.qbs.domain.BackupId
import java.nio.file.Path

class UpdateBackup(
    private val repository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry
) {
    fun execute(backupId: BackupId, command: UpdateBackupCommand) {
        if (!ssRegistry.isSupported(command.storageService))
            throw IllegalArgumentException("Storage service '${command.storageService}' not supported. Supported " +
                    "services are ${ssRegistry.supportedServices().joinToString(", ")}")

        repository.findById(backupId)?.let {
            val updatedBackup = it.copy(
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
            repository.save(updatedBackup)
        }
    }
}

data class UpdateBackupCommand(
    val name: String, val description: String, val storageService: String,
    val sourceDir: Path, val destinationDir: Path, val username: String? = null,
    val password: String? = null, val nBackupsMax: Int = 15,
    val swSensorMQTT: Boolean = false
)