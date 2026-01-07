package com.github.taixmiguel.qbs.application.port

import com.github.taixmiguel.qbs.domain.BackupId

interface BackupIdGenerator {
    fun generate(): BackupId
}