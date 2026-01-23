package com.github.taixmiguel.qbs.driven.storage.local

import com.github.taixmiguel.qbs.application.port.storage.StorageRepository
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@ApplicationScoped
class LocalStorageRepository: StorageRepository {
    override suspend fun push(pathToUpload: Path, file: File) {
        val source = Path.of(file.absolutePath)
        val target = Path.of(pathToUpload.toString(), file.name)

        withContext(Dispatchers.IO) {
            Files.createDirectories(pathToUpload)
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    override suspend fun pull(path: Path, filename: String): File? {
        val file = File(path.toString(), filename)
        return if (file.exists()) file else null
    }

    override suspend fun remove(path: Path, filename: String) {
        val file = File(path.toString(), filename)
        if (file.exists() && !file.delete())
            throw java.io.IOException("Failed to delete file: $file")
    }
}