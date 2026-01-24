package com.github.taixmiguel.qbs.driven.persistence

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.BackupInstance
import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath
import com.github.taixmiguel.qbs.driven.persistence.entity.BackupEntryJpa
import com.github.taixmiguel.qbs.driven.persistence.entity.BackupInstanceEntryJpa
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import kotlin.String

@ApplicationScoped
class BackupRepositoryPanacheJPA: BackupRepository {

    private val panacheBckRepository = object : PanacheRepositoryBase<BackupEntryJpa, String> {}
    private val panacheBckInstanceRepository = object : PanacheRepositoryBase<BackupInstanceEntryJpa, String> {}

    @Transactional
    override fun save(backup: Backup) {
        val entity = BackupEntryJpa().apply {
            id = backup.id.value
            name = backup.name
            description = backup.description
            storageService = backup.storageService
            sourceDir = backup.sourceDir.value
            destinationDir = backup.destinationDir.value
            username = backup.username
            password = backup.password
            nBackupsMax = backup.nBackupsMax
            swSensorMQTT = backup.swSensorMQTT
        }
        panacheBckRepository.persist(entity)
    }

    @Transactional
    override fun findById(id: BackupId): Backup? {
        return panacheBckRepository.findById(id.value)?.let { toDomain(it) }
    }

    @Transactional
    override fun findAll(): List<Backup> {
        return panacheBckRepository.listAll().map { toDomain(it) }
    }

    @Transactional
    override fun save(backup: BackupInstance) {
        val backupEntity = panacheBckRepository.findById(backup.backup.id.value)
            ?: throw IllegalStateException("Backup not found for id: ${backup.backup.id.value}")

        val entity = BackupInstanceEntryJpa()
        entity.id = backup.id
        entity.backup = backupEntity
        entity.name = backup.name
        entity.size = backup.size
        entity.state = backup.state
        entity.createdAt = backup.createdAt
        entity.duration = backup.duration

        panacheBckInstanceRepository.persist(entity)
    }

    private fun toDomain(entity: BackupEntryJpa): Backup {
        val backup = Backup(
            id = BackupId(entity.id),
            name = entity.name,
            description = entity.description,
            storageService = entity.storageService,
            sourceDir = DirectoryPath(entity.sourceDir),
            destinationDir = DirectoryPath(entity.destinationDir),
            username = entity.username,
            password = entity.password,
            nBackupsMax = entity.nBackupsMax,
            swSensorMQTT = entity.swSensorMQTT
        )
        entity.instances.forEach { backup.add(toDomain(backup, it)) }
        return backup
    }

    private fun toDomain(backup: Backup, entity: BackupInstanceEntryJpa): BackupInstance {
        return BackupInstance(
            id = entity.id,
            backup = backup,
            name = entity.name,
            size = entity.size,
            state = entity.state,
            createdAt = entity.createdAt,
            duration = entity.duration
        )
    }
}