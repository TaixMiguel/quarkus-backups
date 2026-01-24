package com.github.taixmiguel.qbs.domain.valueobjects

data class DirectoryPath(val value: String) {
    init {
        require(value.isNotBlank()) { "Path cannot be blank" }
        require(!value.contains("..")) { "Path cannot contain parent directory references" }
    }
    fun toPath(): java.nio.file.Path = java.nio.file.Paths.get(value)
    fun toFile(): java.io.File = java.io.File(value)
}
