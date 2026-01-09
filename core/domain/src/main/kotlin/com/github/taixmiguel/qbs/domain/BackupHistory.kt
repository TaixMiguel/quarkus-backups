package com.github.taixmiguel.qbs.domain

data class BackupHistory(
    val name: String,
    val size: Long,
    val state: BackupState,
    val duration: Float
)
