package com.github.taixmiguel.qbs.driven.storage.mega

import com.github.taixmiguel.qbs.application.port.storage.StorageRepository
import com.github.taixmiguel.qbs.application.port.storage.StorageServiceProvider
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class MegaStorageProvider: StorageServiceProvider {
    @Inject private lateinit var repository: MegaStorageRepository

    override fun name(): String = "mega"
    override fun repository(): StorageRepository = repository
}