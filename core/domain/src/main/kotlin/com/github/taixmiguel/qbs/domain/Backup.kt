package com.github.taixmiguel.qbs.domain

import com.github.taixmiguel.qbs.domain.valueobjects.BackupDescription
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.valueobjects.BackupName
import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath

data class Backup(
    val id: BackupId,
    val name: BackupName,
    val description: BackupDescription,
    val storageService: String,
    val sourceDir: DirectoryPath,
    val destinationDir: DirectoryPath,
    val username: String? = null,
    val password: String? = null,
    val nBackupsMax: Int = 15,
    val swSensorMQTT: Boolean = false,
    private val _instances: MutableList<BackupInstance> = mutableListOf()
) {
    val instances: List<BackupInstance> get() = _instances.toList()

    init {
        require(storageService.isNotBlank()) { "El servicio de almacenaje no puede estar vacío" }
        require(nBackupsMax > 0) { "El parámetro nBackupsMax debe ser superior a 0" }
    }

    fun add(instance: BackupInstance) {
        require(instance.backup.id == this.id) {
            "Cannot add instance from different backup"
        }
        _instances.add(instance)
    }

    override fun toString(): String {
        return "Backup(id=$id, name='${name.value}', description='${description.value}', " +
                "storageService='$storageService', sourceDir=$sourceDir, destinationDir=$destinationDir, " +
                "username=$username, nBackupsMax=$nBackupsMax, swSensorMQTT=$swSensorMQTT, instances=$instances)"
    }
}
