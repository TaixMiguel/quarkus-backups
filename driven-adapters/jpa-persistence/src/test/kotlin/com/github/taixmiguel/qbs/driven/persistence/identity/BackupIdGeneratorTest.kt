package com.github.taixmiguel.qbs.driven.persistence.identity

import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.util.*

class BackupIdGeneratorTest {

    @Inject
    lateinit var idGenerator: UUIDBackupIdGenerator

    @Test
    fun `should create a id backup`() {
        val backupId = idGenerator.generate()
        
        assertNotNull(backupId)
        assertDoesNotThrow {
            UUID.fromString(backupId.value)
        }
    }
}