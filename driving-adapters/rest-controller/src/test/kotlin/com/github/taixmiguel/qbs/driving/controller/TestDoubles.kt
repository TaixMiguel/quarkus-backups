package com.github.taixmiguel.qbs.driving.controller

import com.github.taixmiguel.qbs.application.port.*
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class DummyBackupIdGenerator : BackupIdGenerator {
    override fun generate() = BackupId("test-id")
}

@ApplicationScoped
class DummyBackupRepository : BackupRepository {
    override fun save(backup: Backup) {}
    override fun findById(id: BackupId): Backup? = null
    override fun findAll(): List<Backup> = emptyList()
}

@ApplicationScoped
class DummyStorageServiceRegistry : StorageServiceRegistry {
    override fun isSupported(storageService: String) = true
    override fun supportedServices(): Set<String> = setOf("MEGA")
    override fun getRepository(storageService: String): StorageRepository? = null
}
