package com.github.taixmiguel.qbs.driven.filesystem

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class ZipBackupCompressorTest {

    @Test
    fun `should ignore broken symlinks during compression`(@TempDir tempDir: Path) {
        // Create a regular file
        val regularFile = tempDir.resolve("regular.txt")
        Files.writeString(regularFile, "content")

        // Create a broken symlink
        val brokenLink = tempDir.resolve("broken_link")
        // Target doesn't need to exist for the link to be created
        val target = tempDir.resolve("non_existent_target")
        
        try {
            Files.createSymbolicLink(brokenLink, target)
        } catch (e: Exception) {
            // If we can't create symlinks (e.g. permissions), we can't test this scenario perfectly,
            // but on Linux valid environment it should work.
            println("Could not create symlink: ${e.message}")
            return 
        }

        val compressor = ZipBackupCompressor()
        
        // This is expected to fail with the current implementation if it doesn't handle broken symlinks
        val backupFile = compressor.compress(tempDir)
        
        assertTrue(backupFile.exists())
        assertTrue(backupFile.length() > 0)
    }

    @Test
    fun `should ignore files with no read permissions`(@TempDir tempDir: Path) {
        val restrictedFile = tempDir.resolve("restricted.txt")
        Files.writeString(restrictedFile, "secret")
        
        val file = restrictedFile.toFile()
        // Remove read permissions
        val success = file.setReadable(false)
        
        if (!success) {
            println("Could not remove read permissions, skipping test")
            return
        }

        try {
            val compressor = ZipBackupCompressor()
            val backupFile = compressor.compress(tempDir)
            
            assertTrue(backupFile.exists())
            // Verification: The zip should exist and be empty, as the only file was not readable.
            java.util.zip.ZipFile(backupFile).use { zip ->
                assertTrue(zip.entries().hasMoreElements().not(), "Zip file should be empty")
            }
        } finally {
            // Restore permissions so cleanup can happen
            file.setReadable(true)
        }
    }
}
