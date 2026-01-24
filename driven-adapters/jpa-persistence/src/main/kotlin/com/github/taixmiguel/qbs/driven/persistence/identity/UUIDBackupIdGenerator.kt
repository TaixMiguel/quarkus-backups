package com.github.taixmiguel.qbs.driven.persistence.identity

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class UUIDBackupIdGenerator: BackupIdGenerator {
    override fun generate(): BackupId = BackupId(UUID.randomUUID().toString())
}