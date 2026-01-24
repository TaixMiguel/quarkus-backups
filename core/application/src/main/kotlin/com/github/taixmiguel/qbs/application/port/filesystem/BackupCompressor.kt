package com.github.taixmiguel.qbs.application.port.filesystem

import com.github.taixmiguel.qbs.domain.valueobjects.DirectoryPath
import java.io.File

interface BackupCompressor {
    fun compress(sourceDir: DirectoryPath): File
}