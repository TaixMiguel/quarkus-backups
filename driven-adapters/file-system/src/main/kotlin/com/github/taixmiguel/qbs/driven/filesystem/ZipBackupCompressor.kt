package com.github.taixmiguel.qbs.driven.filesystem

import com.github.taixmiguel.qbs.application.port.filesystem.BackupCompressor
import jakarta.enterprise.context.ApplicationScoped
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@ApplicationScoped
class ZipBackupCompressor: BackupCompressor {
    override fun compress(sourceDir: Path): File {
        val backupFile = Files.createTempFile("backup_", ".bck").toFile()
        val sourceDirFile = sourceDir.toFile()

        backupFile.parentFile?.takeIf { !it.exists() }?.mkdirs()
        ZipOutputStream(FileOutputStream(backupFile)).use { zipOut ->
            val outputCanonical = backupFile.canonicalPath
            sourceDirFile.walkTopDown().forEach { file ->
                if (file.canonicalPath == outputCanonical) return@forEach
                val zipEntryName = file.relativeTo(sourceDirFile).invariantSeparatorsPath

                if (file.isDirectory) {
                    if (zipEntryName.isNotEmpty()) {
                        zipOut.putNextEntry(ZipEntry("$zipEntryName/"))
                        zipOut.closeEntry()
                    }
                } else if (file.isFile && file.canRead()) {
                    try {
                        val entry = ZipEntry(zipEntryName)
                        zipOut.putNextEntry(entry)
                        file.inputStream().use { input -> input.copyTo(zipOut) }
                        zipOut.closeEntry()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return backupFile
    }
}