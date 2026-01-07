package com.github.taixmiguel.qbs.domain

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isReadable
import kotlin.io.path.isWritable

data class Backup(
    val id: BackupId,
    val name: String,
    val description: String,
    val storageService: String,
    val sourceDir: Path,
    val destinationDir: Path,
    val username: String? = null,
    val password: String? = null,
    val nBackupsMax: Int = 15,
    val swSensorMQTT: Boolean = false,
    val history: MutableList<BackupHistory> = mutableListOf()
) {
    init {
        require(name.isNotBlank()) { "El nombre no puede estar vacío" }
        require(description.isNotBlank()) { "La descripción no puede estar vacía" }
        require(storageService.isNotBlank()) { "El servicio de almacenaje no puede estar vacío" }

        require(sourceDir.exists()) { "La ruta no existe: $sourceDir" }
        require(sourceDir.isDirectory()) { "La ruta no es un directorio: $sourceDir" }
        require(sourceDir.isReadable()) { "No tienes permiso de lectura en: $sourceDir" }

        require(destinationDir.exists()) { "La ruta no existe: $destinationDir" }
        require(destinationDir.isDirectory()) { "La ruta no es un directorio: $destinationDir" }
        require(destinationDir.isWritable()) { "No tienes permiso de escritura en: $destinationDir" }
    }

    fun addHistory(history: BackupHistory) {
        this.history.add(history)
    }
}
