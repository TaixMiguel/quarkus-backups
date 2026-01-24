package com.github.taixmiguel.qbs.driven.persistence

import com.github.taixmiguel.qbs.domain.Backup
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

import java.nio.file.Files

@QuarkusTest
class BackupRepositoryPanacheJPATest {

    @Inject
    lateinit var repository: BackupRepositoryPanacheJPA

    @Test
    @Transactional
    fun `should persist and find a backup`() {
        val tempSource = Files.createTempDirectory("backup-source")
        val backupId = BackupId("test-id")
        val backup = Backup(
            id = backupId,
            name = "Test Backup",
            description = "A backup for testing",
            storageService = "local-fs",
            sourceDir = DirectoryPath(tempSource.toString()),
            destinationDir = DirectoryPath("/tmp/dest")
        )

        repository.save(backup)
        val foundBackup = repository.findById(backupId)

        assertNotNull(foundBackup)
        assertEquals(backup.id, foundBackup!!.id)
        assertEquals(backup.name, foundBackup.name)
        assertEquals(backup.description, foundBackup.description)
        assertEquals(backup.storageService, foundBackup.storageService)
        assertEquals(backup.sourceDir.toString(), foundBackup.sourceDir.toString())
        assertEquals(backup.destinationDir.toString(), foundBackup.destinationDir.toString())
    }
}
