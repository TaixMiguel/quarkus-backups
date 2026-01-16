package com.github.taixmiguel.qbs

import com.github.taixmiguel.qbs.application.port.StorageRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceProvider
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import java.util.stream.Collectors

@ApplicationScoped
class CdiStorageServiceRegistry @Inject constructor(
    private val providers: Instance<StorageServiceProvider>
): StorageServiceRegistry {
    private final var supported: Set<String> = providers.stream()
        .map(StorageServiceProvider::name)
        .collect(Collectors.toUnmodifiableSet())

    override fun isSupported(storageService: String): Boolean = supported.contains(storageService)
    override fun supportedServices(): Set<String> = supported
    override fun getRepository(storageService: String): StorageRepository? {
        return providers.stream()
            .filter { it.name() == storageService }
            .findFirst()
            .map { it.repository() }
            .orElse(null)
    }
}