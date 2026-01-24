package com.github.taixmiguel.qbs.domain.valueobjects

data class BackupName(val value: String) {
    init {
        require(value.isNotBlank()) { "Backup name cannot be blank" }
        require(value.length in 3..100) { "Backup name must be between 3 and 100 characters" }
        require(value.matches(Regex("^[a-zA-Z0-9 _-]+$"))) {
            "Backup name can only contain alphanumeric characters, hyphens, and underscores"
        }
    }
}