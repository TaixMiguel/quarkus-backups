package com.github.taixmiguel.qbs.driving.controller.dto

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@QuarkusTest
class CreateBackupRequestTest {

    @Inject
    lateinit var mapper: ObjectMapper

    @Test
    fun `should deserialize nBackupsMax correctly`() {
        val json = """
            {
                "name": "Test Backup",
                "description": "Test Description",
                "storageService": "MEGA",
                "sourceDir": "/tmp/source",
                "destinationDir": "/tmp/dest",
                "NBackupsMax": 5
            }
        """

        val request = mapper.readValue(json, CreateBackupRequest::class.java)
        assertEquals(5, request.nBackupsMax)
    }
}