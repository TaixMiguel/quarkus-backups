package com.github.taixmiguel.qbs.application.port.storage

import java.io.File
import java.nio.file.Path

interface StorageRepository {
    suspend fun push(pathToUpload: Path, file: File)
    suspend fun pull(path: Path, filename: String): File?
    suspend fun remove(path: Path, filename: String)
}