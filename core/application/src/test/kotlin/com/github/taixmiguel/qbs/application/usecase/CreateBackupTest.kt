package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.application.port.StorageRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.commands.BackupCommand
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class CreateBackupTest {
    private val generatedId = BackupId("backup-id")

    private val repository = FakeBackupRepository()
    private val idGenerator = FakeBackupIdGenerator(generatedId)
    private val ssRegistry = FakeStorageServiceRegistry()

    private val createBackup = CreateBackup(repository = repository, idGenerator = idGenerator,
                                ssRegistry = ssRegistry)

    @Test
    fun `should create and persist a backup`() {
        val command = BackupCommand(name = "backup", description = "backup description",
            storageService = "local storage", sourceDir = Path("src"), destinationDir = Path("dst"))
        createBackup.execute(command)
        val savedBackup = repository.savedBackup

        assertNotNull(savedBackup)
        assertEquals(generatedId, savedBackup!!.id)
        assertEquals("backup", savedBackup.name)

        assertEquals("backup description", savedBackup.description)
        assertEquals("local storage", savedBackup.storageService)
        assertEquals("src", savedBackup.sourceDir.toString())
        assertEquals("dst", savedBackup.destinationDir.toString())
        assertNull(savedBackup.username)
        assertNull(savedBackup.password)
        assertEquals(15, savedBackup.nBackupsMax)
        assertFalse(savedBackup.swSensorMQTT)
    }

    private class FakeBackupRepository: BackupRepository {
        var savedBackup: Backup? = null

        override fun save(backup: Backup) {
            savedBackup = backup
        }

        override fun findById(id: BackupId): Backup? {
            TODO("Not yet implemented")
        }

        override fun findAll(): List<Backup> {
            TODO("Not yet implemented")
        }
    }

    private class FakeBackupIdGenerator(
        private val id: BackupId
    ): BackupIdGenerator {
        override fun generate(): BackupId = id
    }

    private class FakeStorageServiceRegistry: StorageServiceRegistry {
        val supported: MutableSet<String> = mutableSetOf("local storage")

        override fun isSupported(storageService: String): Boolean = supported.contains(storageService)
        override fun supportedServices(): Set<String> = supported
        override fun getRepository(storageService: String): StorageRepository? {
            TODO("Not yet implemented")
        }
    }
}