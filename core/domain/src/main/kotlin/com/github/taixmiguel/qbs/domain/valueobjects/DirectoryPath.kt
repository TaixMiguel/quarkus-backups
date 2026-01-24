package com.github.taixmiguel.qbs.domain.valueobjects

@JvmInline
value class DirectoryPath private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): DirectoryPath {
            val trimmedValue = value.trim()
            require(value.isNotBlank()) { "Path cannot be blank" }
            require(!value.contains("..")) { "Path cannot contain parent directory references" }
            return DirectoryPath(trimmedValue)
        }
    }
    fun toPath(): java.nio.file.Path = java.nio.file.Paths.get(value)
    fun toFile(): java.io.File = java.io.File(value)
}