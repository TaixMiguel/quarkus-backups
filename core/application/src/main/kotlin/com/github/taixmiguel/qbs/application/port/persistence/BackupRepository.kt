package com.github.taixmiguel.qbs.application.port.persistence

import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId

interface BackupRepository {
    fun save(backup: Backup)
    fun findById(id: BackupId): Backup?
    fun findAll(): List<Backup>
}