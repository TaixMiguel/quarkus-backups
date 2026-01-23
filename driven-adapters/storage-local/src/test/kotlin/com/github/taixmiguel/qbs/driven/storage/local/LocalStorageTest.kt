package com.github.taixmiguel.qbs.driven.storage.local

import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.io.File
import kotlin.io.path.Path

@QuarkusTest
class LocalStorageTest {
    @Inject
    lateinit var repository: LocalStorageRepository

    @Test
    fun `should push file successfully`() = runBlocking {
        val target = Path("/example/test")
        val filepath = File("quarkus-backups/driven-adapters/storage-mega/src/main/resources/application.properties")

        repository.push(target, filepath)
    }

    @Test
    fun `should pull file successfully`() = runBlocking {
        val path = Path("/example/test")
        val filename = "application.properties"

        val result = repository.pull(path, filename)
        assertNotNull(result)
    }

    @Test
    fun `should remove file successfully`() = runBlocking {
        val path = Path("/example/test")
        val filename = "application.properties"

        repository.remove(path, filename)
    }
}