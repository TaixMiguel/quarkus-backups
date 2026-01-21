package com.github.taixmiguel.qbs.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class BackupTest {
    @Test
    fun `should create a backup with default values`() {
        val backup = Backup(
            id = BackupId("backup-1"),
            name = "Backup name",
            description = "Backup description",
            storageService = "storage-service",
            sourceDir = Path("src"),
            destinationDir = Path("src")
        )

        assertNotNull(backup)
        assertNull(backup.username)
        assertNull(backup.password)
        assertEquals(15, backup.nBackupsMax)
        assertFalse(backup.swSensorMQTT)
        assertEquals(0, backup.instances.size)
    }

    @Test
    fun `should not allow empty name`() {
        assertThrows(IllegalArgumentException::class.java) {
            Backup(
                id = BackupId("backup-2"),
                name = "",
                description = "",
                storageService = "",
                sourceDir = Path("src"),
                destinationDir = Path("src")
            )
        }
    }

    @Test
    fun `should not allow empty description`() {
        assertThrows(IllegalArgumentException::class.java) {
            Backup(
                id = BackupId("backup-3"),
                name = "Backup name",
                description = "",
                storageService = "",
                sourceDir = Path("src"),
                destinationDir = Path("src")
            )
        }
    }

    @Test
    fun `should not allow empty storage service`() {
        assertThrows(IllegalArgumentException::class.java) {
            Backup(
                id = BackupId("backup-4"),
                name = "Backup name",
                description = "Backup description",
                storageService = "",
                sourceDir = Path("src"),
                destinationDir = Path("src")
            )
        }
    }
}
