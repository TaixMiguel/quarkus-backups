package com.github.taixmiguel.qbs.domain.valueobjects

@JvmInline
value class BackupName private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): BackupName {
            val trimmedValue = value.trim()
            require(trimmedValue.isNotBlank()) { "Backup name cannot be blank" }
            require(trimmedValue.length in 3..100) { "Backup name must be between 3 and 100 characters" }
            require(trimmedValue.matches(Regex("^[a-zA-Z0-9 _-]+$"))) {
                "Backup name can only contain alphanumeric characters, hyphens, and underscores"
            }
            return BackupName(trimmedValue)
        }
    }
}