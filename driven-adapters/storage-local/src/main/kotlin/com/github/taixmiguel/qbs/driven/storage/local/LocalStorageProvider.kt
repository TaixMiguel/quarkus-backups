package com.github.taixmiguel.qbs.driven.storage.local

import com.github.taixmiguel.qbs.application.port.storage.StorageRepository
import com.github.taixmiguel.qbs.application.port.storage.StorageServiceProvider
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class LocalStorageProvider: StorageServiceProvider {
    @Inject private lateinit var repository: LocalStorageRepository

    override fun name(): String = "local"
    override fun repository(): StorageRepository = repository
}