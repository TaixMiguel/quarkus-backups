package com.github.taixmiguel.qbs.config

import com.github.taixmiguel.qbs.application.port.filesystem.BackupCompressor
import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.filesystem.FileSystemValidator
import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.application.port.publisher.MessagePublisher
import com.github.taixmiguel.qbs.application.port.storage.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.CreateBackup
import com.github.taixmiguel.qbs.application.usecase.ExecuteBackup
import com.github.taixmiguel.qbs.application.usecase.ListBackups
import com.github.taixmiguel.qbs.application.usecase.SearchBackup
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ApplicationScoped
class UseCaseConfiguration {
    @Produces
    @ApplicationScoped
    fun createBackup(
        idGenerator: BackupIdGenerator,
        repository: BackupRepository,
        ssRegistry: StorageServiceRegistry,
        fsValidator: FileSystemValidator
    ): CreateBackup {
        return CreateBackup(idGenerator, repository, ssRegistry, fsValidator)
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
        ssRegistry: StorageServiceRegistry,
        compressor: BackupCompressor,
        msgPublisher: MessagePublisher
    ): ExecuteBackup {
        return ExecuteBackup(repository, ssRegistry, compressor, msgPublisher)
    }
}