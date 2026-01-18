package com.github.taixmiguel.qbs.driven.persistence

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import com.github.taixmiguel.qbs.driven.persistence.entity.BackupEntryJpa
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.nio.file.Path

@ApplicationScoped
class BackupRepositoryPanacheJPA : BackupRepository {

    private val panacheRepository = object : PanacheRepositoryBase<BackupEntryJpa, String> {}

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
        panacheRepository.persist(entity)
    }

    override fun findById(id: BackupId): Backup? {
        return panacheRepository.findById(id.value)?.let { toDomain(it) }
    }

    override fun findAll(): List<Backup> {
        return panacheRepository.listAll().map { toDomain(it) }
    }

    private fun toDomain(entity: BackupEntryJpa): Backup {
        return Backup(
            id = BackupId(entity.id),
            name = entity.name,
            description = entity.description,
            storageService = entity.storageService,
            sourceDir = Path.of(entity.sourceDir),
            destinationDir = Path.of(entity.destinationDir),
            username = entity.username,
            password = entity.password,
            nBackupsMax = entity.nBackupsMax,
            swSensorMQTT = entity.swSensorMQTT,
            // TODO: Map history from the entity. The BackupEntryJpa entity needs to be updated to store history.
            history = listOf()
        )
    }
}