package com.github.taixmiguel.qbs.driven.filesystem

import com.github.taixmiguel.qbs.application.port.BackupCompressor
import jakarta.enterprise.context.ApplicationScoped
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@ApplicationScoped
class ZipBackupCompressor: BackupCompressor {
    override fun compress(sourceDir: Path, output: File) {
        val sourceDir = File(sourceDir.toString())

        ZipOutputStream(FileOutputStream(output.canonicalPath)).use { zipOut ->
            val outputCanonical = output.canonicalPath
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