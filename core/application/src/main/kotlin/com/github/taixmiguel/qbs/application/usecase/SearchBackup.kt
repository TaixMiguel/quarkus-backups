package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId

class SearchBackup(private val repository: BackupRepository) {
    fun execute(backupId: BackupId): Backup? {
        return repository.findById(backupId)
    }
}
