package com.github.taixmiguel.qbs.driven.storage.mega

import com.github.taixmiguel.qbs.application.port.StorageServiceProvider
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MegaStorageProvider: StorageServiceProvider {
    override fun name(): String = "mega"
}