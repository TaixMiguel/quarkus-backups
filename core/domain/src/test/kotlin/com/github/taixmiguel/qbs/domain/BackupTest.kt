package com.github.taixmiguel.qbs.domain

import com.github.taixmiguel.qbs.domain.valueobjects.BackupDescription
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.valueobjects.BackupName
import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class BackupTest {
    @Test
    fun `should create a backup with default values`() {
        val backup = Backup(
            id = BackupId("backup-1"),
            name = BackupName("Backup name"),
            description = BackupDescription("Backup description"),
            storageService = "storage-service",
            sourceDir = DirectoryPath("src"),
            destinationDir = DirectoryPath("src")
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
                name = BackupName(""),
                description = BackupDescription(""),
                storageService = "",
                sourceDir = DirectoryPath("src"),
                destinationDir = DirectoryPath("src")
            )
        }
    }

    @Test
    fun `should not allow empty description`() {
        assertThrows(IllegalArgumentException::class.java) {
            Backup(
                id = BackupId("backup-3"),
                name = BackupName("Backup name"),
                description = BackupDescription(""),
                storageService = "",
                sourceDir = DirectoryPath("src"),
                destinationDir = DirectoryPath("src")
            )
        }
    }

    @Test
    fun `should not allow empty storage service`() {
        assertThrows(IllegalArgumentException::class.java) {
            Backup(
                id = BackupId("backup-4"),
                name = BackupName("Backup name"),
                description = BackupDescription("Backup description"),
                storageService = "",
                sourceDir = DirectoryPath("src"),
                destinationDir = DirectoryPath("src")
            )
        }
    }
}
