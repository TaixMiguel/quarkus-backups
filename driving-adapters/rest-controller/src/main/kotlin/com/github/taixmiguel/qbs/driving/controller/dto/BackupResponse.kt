package com.github.taixmiguel.qbs.driving.controller.dto

import com.github.taixmiguel.qbs.domain.Backup
import kotlin.String

data class BackupResponse(
    val id: String,
    val name: String,
    val description: String,
    val storageService: String,
    val sourceDir: String,
    val destinationDir: String,
    val nBackupsMax: Int,
    val swSensorMQTT: Boolean
) {
    companion object {
        fun from(backup: Backup): BackupResponse =
            BackupResponse(
                id = backup.id.value,
                name = backup.name.value,
                description = backup.description.value,
                storageService = backup.storageService,
                sourceDir = backup.sourceDir.toString(),
                destinationDir = backup.destinationDir.toString(),
                nBackupsMax = backup.nBackupsMax,
                swSensorMQTT = backup.swSensorMQTT
            )
    }
}

