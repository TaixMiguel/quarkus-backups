package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup

class ListBackups(private val repository: BackupRepository) {
    fun execute(): List<Backup> = repository.findAll()
}
