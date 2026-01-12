package com.github.taixmiguel.qbs.application.port

interface StorageServiceRegistry {
    fun isSupported(storageService: String): Boolean
    fun supportedServices(): Set<String>
    fun getRepository(storageService: String): StorageRepository?
}