package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.filesystem.FileSystemValidator
import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.application.port.storage.StorageRepository
import com.github.taixmiguel.qbs.application.port.storage.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.commands.BackupCommand
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.BackupInstance
import com.github.taixmiguel.qbs.domain.valueobjects.BackupDescription
import com.github.taixmiguel.qbs.domain.valueobjects.BackupName
import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UpdateBackupTest {
    private val generatedId = BackupId("backup-id")

    private val repository = FakeBackupRepository()
    private val idGenerator = FakeBackupIdGenerator(generatedId)
    private val ssRegistry = FakeStorageServiceRegistry()
    private val fsValidator = FakeFileSystemValidator()

    private val createBackup = CreateBackup(repository = repository, idGenerator = idGenerator,
                                ssRegistry = ssRegistry, fileSystemValidator = fsValidator)
    private val updateBackup = UpdateBackup(repository = repository, ssRegistry = ssRegistry,
                                fileSystemValidator = fsValidator)

    @Test
    fun `should update and persist a backup`() {
        val cCommand = BackupCommand(name = "backup", description = "backup description",
            storageService = "local storage", sourceDir = "src", destinationDir = "dst")
        createBackup.execute(cCommand)
        val savedBackup = repository.savedBackup

        val uCommand = BackupCommand(name = "backup-updated", description = "backup description updated",
            storageService = "local storage", sourceDir = "src", destinationDir = "dst")
        updateBackup.execute(savedBackup!!.id, uCommand)
        val updatedBackup = repository.savedBackup

        assertNotNull(updatedBackup)
        assertEquals(generatedId, updatedBackup!!.id)
        assertEquals(BackupName("backup-updated"), updatedBackup.name)
        assertEquals(BackupDescription("backup description updated"), updatedBackup.description)
        assertEquals("local storage", updatedBackup.storageService)
        assertEquals(DirectoryPath("src"), updatedBackup.sourceDir)
        assertEquals(DirectoryPath("dst"), updatedBackup.destinationDir)
        assertNull(updatedBackup.username)
        assertNull(updatedBackup.password)
        assertEquals(15, updatedBackup.nBackupsMax)
        assertFalse(updatedBackup.swSensorMQTT)
    }

    private class FakeBackupRepository: BackupRepository {
        var savedBackup: Backup? = null

        override fun save(backup: Backup) {
            savedBackup = backup
        }

        override fun findById(id: BackupId): Backup? {
            return if (savedBackup?.id == id) savedBackup else null
        }

        override fun findAll(): List<Backup> {
            TODO("Not yet implemented")
        }

        override fun save(bckInstance: BackupInstance) {
            TODO("Not yet implemented")
        }

        override fun delete(bckInstance: BackupInstance) {
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

    private class FakeFileSystemValidator: FileSystemValidator {
        override fun validateDirectory(directory: String) {}
    }
}