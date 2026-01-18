package com.github.taixmiguel.qbs.driven.persistence.identity

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.*

class BackupIdGeneratorTest {
    private val idGenerator = UUIDBackupIdGenerator()

    @Test
    fun `should create a id backup`() {
        val backupId = idGenerator.generate()
        
        assertNotNull(backupId)
        assertDoesNotThrow {
            UUID.fromString(backupId.value)
        }
    }
}