package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.filesystem.FileSystemValidator
import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.application.port.storage.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.commands.BackupCommand
import com.github.taixmiguel.qbs.domain.valueobjects.BackupDescription
import com.github.taixmiguel.qbs.domain.valueobjects.BackupName
import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath

class CreateBackup(
    private val idGenerator: BackupIdGenerator,
    private val repository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry,
    private val fileSystemValidator: FileSystemValidator
) {
    fun execute(command: BackupCommand): BackupId {
        fileSystemValidator.validateDirectory(command.sourceDir)

        if (!ssRegistry.isSupported(command.storageService))
            throw IllegalArgumentException("Storage service '${command.storageService}' not supported. Supported " +
                    "services are ${ssRegistry.supportedServices().joinToString(", ")}")

        val backup = Backup(
            id = idGenerator.generate(),
            name = BackupName(command.name),
            description = BackupDescription(command.description),
            storageService = command.storageService,
            sourceDir = DirectoryPath(command.sourceDir),
            destinationDir = DirectoryPath(command.destinationDir),
            username = command.username,
            password = command.password,
            nBackupsMax = command.nBackupsMax,
            swSensorMQTT = command.swSensorMQTT
        )

        repository.save(backup)
        return backup.id
    }
}
