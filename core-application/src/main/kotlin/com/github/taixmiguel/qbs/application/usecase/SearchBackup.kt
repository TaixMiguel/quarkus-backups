package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId

data class SearchBackup(private val repository: BackupRepository) {
    fun execute(backupId: BackupId): Backup? {
        return repository.findById(backupId)
    }
}
