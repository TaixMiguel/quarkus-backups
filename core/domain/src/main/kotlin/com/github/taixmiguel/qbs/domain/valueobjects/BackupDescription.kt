package com.github.taixmiguel.qbs.domain.valueobjects

data class BackupDescription(val value: String) {
    init {
        require(value.isNotBlank()) { "Description cannot be blank" }
        require(value.length <= 500) { "Description cannot exceed 500 characters" }
    }
}