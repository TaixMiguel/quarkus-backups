package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.persistence.BackupRepository
import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.BackupId
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class SearchBackupTest {
    private val repository = FakeBackupRepository()

    @Test
    fun `should get a backup`() {
        val backup = SearchBackup(repository).execute(BackupId("backup-1"))
        assertNotNull(backup)
    }

    @Test
    fun `shouldn't get a backup`() {
        val backup = SearchBackup(repository).execute(BackupId("backup-2"))
        assertNull(backup)
    }

    private class FakeBackupRepository: BackupRepository {
        var backups: MutableList<Backup> = mutableListOf()
        init {
            backups.add(Backup(id = BackupId("backup-1"), name = "backup one", description = "backup description", storageService = "local storage",
                sourceDir = Path("src"), destinationDir = Path("dst")))
        }

        override fun save(backup: Backup) {
            TODO("Not yet implemented")
        }

        override fun findById(id: BackupId): Backup? {
            return backups.firstOrNull { it.id == id }
        }

        override fun findAll(): List<Backup> {
            TODO("Not yet implemented")
        }
    }
}