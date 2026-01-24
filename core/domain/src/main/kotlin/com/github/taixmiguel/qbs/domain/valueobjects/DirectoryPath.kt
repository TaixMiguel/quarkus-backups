package com.github.taixmiguel.qbs.domain.valueobjects

data class DirectoryPath(val value: String) {
    init {
        require(value.isNotBlank()) { "Path cannot be blank" }
        require(!value.contains("..")) { "Path cannot contain parent directory references" }
    }
}
