package com.github.taixmiguel.qbs.config

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.CreateBackup
import com.github.taixmiguel.qbs.application.usecase.ExecuteBackup
import com.github.taixmiguel.qbs.application.usecase.ListBackups
import com.github.taixmiguel.qbs.application.usecase.SearchBackup
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Produces

class UseCaseConfiguration {
    @Produces
    @ApplicationScoped
    fun createBackup(
        idGenerator: BackupIdGenerator,
        repository: BackupRepository,
        ssRegistry: StorageServiceRegistry
    ): CreateBackup {
        return CreateBackup(idGenerator, repository, ssRegistry)
    }

    @Produces
    @ApplicationScoped
    fun searchBackup(repository: BackupRepository): SearchBackup {
        return SearchBackup(repository)
    }

    @Produces
    @ApplicationScoped
    fun listBackups(repository: BackupRepository): ListBackups {
        return ListBackups(repository)
    }

    @Produces
    @ApplicationScoped
    fun executeBackup(
        repository: BackupRepository,
        ssRegistry: StorageServiceRegistry
    ): ExecuteBackup {
        return ExecuteBackup(repository, ssRegistry)
    }
}