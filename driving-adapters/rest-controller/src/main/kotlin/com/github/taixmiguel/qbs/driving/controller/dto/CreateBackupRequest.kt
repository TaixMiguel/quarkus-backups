package com.github.taixmiguel.qbs.driving.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateBackupRequest(
    val name: String = "",
    val description: String = "",
    val storageService: String = "",
    val sourceDir: String = "",
    val destinationDir: String = "",
    val username: String? = null,
    val password: String? = null,
    @field:JsonProperty("nBackupsMax")
    val nBackupsMax: Int = 0,
    val swSensorMQTT: Boolean = false
)
