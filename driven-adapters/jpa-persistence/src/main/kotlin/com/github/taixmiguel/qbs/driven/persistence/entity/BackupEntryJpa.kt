package com.github.taixmiguel.qbs.driven.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "backups")
class BackupEntryJpa {
    @Id
    lateinit var id: String
    lateinit var name: String
    lateinit var description: String
    lateinit var storageService: String
    lateinit var sourceDir: String
    lateinit var destinationDir: String
    var username: String? = null
    var password: String? = null
    var nBackupsMax: Int = 0
    var swSensorMQTT: Boolean = false

    @OneToMany(mappedBy = "backup")
    val instances: List<BackupInstanceEntryJpa> = listOf()
}