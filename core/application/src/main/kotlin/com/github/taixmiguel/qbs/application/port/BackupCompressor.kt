package com.github.taixmiguel.qbs.application.port

import java.io.File
import java.nio.file.Path

interface BackupCompressor {
    fun compress(sourceDir: Path): File
}