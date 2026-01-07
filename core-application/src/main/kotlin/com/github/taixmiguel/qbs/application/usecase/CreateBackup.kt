package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import java.nio.file.Path

class CreateBackup(
    private val idGenerator: BackupIdGenerator,
    private val repository: BackupRepository
) {
    fun execute(name: String, description: String, storageService: String,
                sourceDir: Path, destinationDir: Path, username: String? = null,
                password: String? = null, nBackupsMax: Int = 15, swSensorMQTT: Boolean = false): BackupId {
        val backupId = idGenerator.generate()

        val backup = Backup(
            id = backupId,
            name = name,
            description = description,
            storageService = storageService,
            sourceDir = sourceDir,
            destinationDir = destinationDir,
            username = username,
            password = password,
            nBackupsMax = nBackupsMax,
            swSensorMQTT = swSensorMQTT
        )

        repository.save(backup)
        return backup.id
    }
}