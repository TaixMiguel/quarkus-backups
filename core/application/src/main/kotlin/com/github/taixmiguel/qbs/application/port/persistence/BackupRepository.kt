package com.github.taixmiguel.qbs.application.port.persistence

import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.BackupInstance

interface BackupRepository {
    fun save(backup: Backup)
    fun findById(id: BackupId): Backup?
    fun findAll(): List<Backup>

    fun save(bckInstance: BackupInstance)
    fun delete(bckInstance: BackupInstance)
}