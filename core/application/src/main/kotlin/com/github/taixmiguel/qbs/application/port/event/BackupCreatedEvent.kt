package com.github.taixmiguel.qbs.application.port.event

import com.github.taixmiguel.qbs.domain.valueobjects.BackupId
import com.github.taixmiguel.qbs.domain.valueobjects.BackupName

data class BackupCreatedEvent(
    val backupId: BackupId,
    val swSensorMQTT: Boolean,
    val backupName: BackupName
)