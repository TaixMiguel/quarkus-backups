package com.github.taixmiguel.qbs.application.port.storage

interface StorageServiceRegistry {
    fun isSupported(storageService: String): Boolean
    fun supportedServices(): Set<String>
    fun getRepository(storageService: String): StorageRepository?
}