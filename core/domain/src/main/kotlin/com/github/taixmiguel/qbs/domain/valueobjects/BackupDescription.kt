package com.github.taixmiguel.qbs.domain.valueobjects

@JvmInline
value class BackupDescription private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): BackupDescription {
            val trimmedValue = value.trim()
            require(value.isNotBlank()) { "Description cannot be blank" }
            require(value.length <= 500) { "Description cannot exceed 500 characters" }
            return BackupDescription(trimmedValue)
        }
    }
}