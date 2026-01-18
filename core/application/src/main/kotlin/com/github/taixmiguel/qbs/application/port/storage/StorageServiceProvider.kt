package com.github.taixmiguel.qbs.application.port.storage

interface StorageServiceProvider {
    fun name(): String
    fun repository(): StorageRepository
}