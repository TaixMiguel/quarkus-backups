package com.github.taixmiguel.qbs.application.usecase.commands

import java.nio.file.Path

data class BackupCommand(
    val name: String, val description: String, val storageService: String,
    val sourceDir: Path, val destinationDir: Path, val username: String? = null,
    val password: String? = null, val nBackupsMax: Int = 15,
    val swSensorMQTT: Boolean = false
)
