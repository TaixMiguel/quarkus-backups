package com.github.taixmiguel.qbs.driven.filesystem

import com.github.taixmiguel.qbs.application.port.filesystem.FileSystemValidator
import jakarta.enterprise.context.ApplicationScoped
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isReadable

@ApplicationScoped
class FileSystemValidatorInstance: FileSystemValidator {
    override fun validateDirectory(directory: String) {
        if (directory.contains("..")) throw IllegalArgumentException("Path cannot contain parent directory references '..'")
        val path = Paths.get(directory)

        if (!path.exists()) throw IllegalArgumentException("Path $directory does not exist")
        if (!path.isDirectory()) throw IllegalArgumentException("Path $directory is not a directory")
        if (!path.isReadable()) throw IllegalArgumentException("Path $directory is not readable")
    }
}