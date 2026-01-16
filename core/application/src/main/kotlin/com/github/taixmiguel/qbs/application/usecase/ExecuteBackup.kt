package com.github.taixmiguel.qbs.application.usecase

import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import com.github.taixmiguel.qbs.domain.BackupId
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExecuteBackup {
    suspend fun execute(backupId: BackupId, backupRepository: BackupRepository, ssRegistry: StorageServiceRegistry) {
        backupRepository.findById(backupId)?.let { backup ->
            ssRegistry.getRepository(backup.storageService)?.let {
                val now = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                val outputPath = "backup_${now.format(formatter)}.bck"

                val backupFile =  File(outputPath)
                try {
                    zipDirectory(backup.sourceDir, outputPath)
                    it.push(backup.destinationDir, backupFile)
                } finally {
                    if (backupFile.exists()) backupFile.delete()
                }
            }
        } ?: throw NoSuchElementException("Backup with id $backupId does not exist")
    }

    private fun zipDirectory(sourcePath: Path, outputPath: String) {
        val sourceDir = File(sourcePath.toString())

        ZipOutputStream(FileOutputStream(outputPath)).use { zipOut ->
            val outputCanonical = File(outputPath).canonicalPath
            sourceDir.walkTopDown().forEach { file ->
                if (file.canonicalPath == outputCanonical) return@forEach
                val zipEntryName = file.relativeTo(sourceDir).invariantSeparatorsPath

                if (file.isDirectory) {
                    if (zipEntryName.isNotEmpty()) {
                        zipOut.putNextEntry(ZipEntry("$zipEntryName/"))
                        zipOut.closeEntry()
                    }
                } else {
                    val entry = ZipEntry(zipEntryName)
                    zipOut.putNextEntry(entry)
                    file.inputStream().use { input -> input.copyTo(zipOut) }
                    zipOut.closeEntry()
                }
            }
        }
    }
}
