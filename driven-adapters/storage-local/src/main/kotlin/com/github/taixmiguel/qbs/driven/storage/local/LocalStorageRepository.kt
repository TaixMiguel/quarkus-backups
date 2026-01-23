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

        try {
            withContext(Dispatchers.IO) {
                Files.createDirectory(pathToUpload)
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun pull(path: Path, filename: String): File? {
        return File(path.toString(), filename)
    }

    override suspend fun remove(path: Path, filename: String) {
        File(path.toString(), filename).delete()
    }
}