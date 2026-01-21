package com.github.taixmiguel.qbs.driven.persistence.entity

import com.github.taixmiguel.qbs.domain.BackupState
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "backup_instances")
class BackupInstanceEntryJpa {
    @Id
    lateinit var id: String
    @ManyToOne
    @JoinColumn(name = "backup_fk")
    lateinit var backup: BackupEntryJpa
    lateinit var name: String
    var size: Long? = null
    lateinit var state: BackupState
    lateinit var createdAt: LocalDateTime
    var duration: Long? = null
}