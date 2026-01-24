package com.github.taixmiguel.qbs.application.usecase.commands

data class BackupCommand(
    val name: String, val description: String, val storageService: String,
    val sourceDir: String, val destinationDir: String, val username: String? = null,
    val password: String? = null, val nBackupsMax: Int = 15,
    val swSensorMQTT: Boolean = false
)
