package com.github.taixmiguel.qbs.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

data class BackupInstance(
    val id: String = UUID.randomUUID().toString(),
    val backup: Backup,
    val name: String,
    var size: Long? = null,  // bytes
    var state: BackupState = BackupState.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
    var duration: Long? = null,
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

        fun create(
            backup: Backup,
            name: String? = null
        ): BackupInstance {
            val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
            val finalName = name ?: run {
                "backup_${now.format(formatter)}.bck"
            }

            return BackupInstance(
                name = finalName,
                backup = backup,
                createdAt = now,
            )
        }
    }
}
