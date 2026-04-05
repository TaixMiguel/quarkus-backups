package com.github.taixmiguel.qbs.application.port.event

import com.github.taixmiguel.qbs.domain.BackupState
import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.valueobjects.BackupName
import java.time.LocalDateTime

data class BackupExecutedEvent(
    val backupId: BackupId,
    val state: BackupState,
    val swSensorMQTT: Boolean,
    val backupName: BackupName,
    val executedAt: LocalDateTime
)