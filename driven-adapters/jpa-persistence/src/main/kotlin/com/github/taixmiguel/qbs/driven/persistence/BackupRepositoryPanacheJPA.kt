package com.github.taixmiguel.qbs.driven.persistence

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import com.github.taixmiguel.qbs.domain.BackupInstance
import com.github.taixmiguel.qbs.driven.persistence.entity.BackupEntryJpa
import com.github.taixmiguel.qbs.driven.persistence.entity.BackupInstanceEntryJpa
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.nio.file.Path
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
            sourceDir = backup.sourceDir.toString()
            destinationDir = backup.destinationDir.toString()
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
    override fun save(bck: BackupInstance) {
        val backupEntity = panacheBckRepository.findById(bck.backup.id.value)
            ?: throw IllegalStateException("Backup not found for id: ${bck.backup.id.value}")

        val entity = BackupInstanceEntryJpa().apply {
            id = bck.id
            backup = backupEntity
            name = bck.name
            size = bck.size
            state = bck.state
            createdAt = bck.createdAt
            duration = bck.duration
        }
        panacheBckInstanceRepository.persist(entity)
    }

    private fun toDomain(entity: BackupEntryJpa): Backup {
        val backup = Backup(
            id = BackupId(entity.id),
            name = entity.name,
            description = entity.description,
            storageService = entity.storageService,
            sourceDir = Path.of(entity.sourceDir),
            destinationDir = Path.of(entity.destinationDir),
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