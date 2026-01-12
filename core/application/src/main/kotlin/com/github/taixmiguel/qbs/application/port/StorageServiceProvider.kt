package com.github.taixmiguel.qbs.application.port

interface StorageServiceProvider {
    fun name(): String
    fun repository(): StorageRepository
}