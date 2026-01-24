package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.BackupInstance
import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ListBackupsTest {
    private val repository = FakeBackupRepository()

    @Test
    fun `should get a backup list`() {
        val backups = ListBackups(repository).execute()

        assertNotNull(backups)
        assertEquals(2, backups.size)
    }

    private class FakeBackupRepository: BackupRepository {
        var backups: MutableList<Backup> = mutableListOf()
        init {
            backups.add(Backup(
                id = BackupId("backup-1"), name = "backup one", description = "backup description", storageService = "local storage",
                sourceDir = DirectoryPath("src"), destinationDir = DirectoryPath("dst")))
            backups.add(Backup(
                id = BackupId("backup-2"), name = "backup two", description = "backup description", storageService = "local storage",
                sourceDir = DirectoryPath("src"), destinationDir = DirectoryPath("dst")))
        }

        override fun save(backup: Backup) {
            TODO("Not yet implemented")
        }

        override fun findById(id: BackupId): Backup? {
            TODO("Not yet implemented")
        }

        override fun findAll(): List<Backup> = backups
        override fun save(backup: BackupInstance) {
            TODO("Not yet implemented")
        }
    }
}