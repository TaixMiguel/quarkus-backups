package com.github.taixmiguel.qbs.driving.controller

import com.github.taixmiguel.qbs.application.port.BackupIdGenerator
import com.github.taixmiguel.qbs.application.port.BackupRepository
import com.github.taixmiguel.qbs.application.port.StorageServiceRegistry
import com.github.taixmiguel.qbs.application.usecase.CreateBackup
import com.github.taixmiguel.qbs.application.usecase.ExecuteBackup
import com.github.taixmiguel.qbs.application.usecase.ListBackups
import com.github.taixmiguel.qbs.application.usecase.SearchBackup
import com.github.taixmiguel.qbs.application.usecase.commands.BackupCommand
import com.github.taixmiguel.qbs.domain.BackupId
import com.github.taixmiguel.qbs.driving.controller.dto.BackupResponse
import com.github.taixmiguel.qbs.driving.controller.dto.CreateBackupRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/backup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class BackupResource(
    private val idGenerator: BackupIdGenerator,
    private val repository: BackupRepository,
    private val ssRegistry: StorageServiceRegistry
) {
    private val createBackup = CreateBackup(idGenerator, repository, ssRegistry)
    private val searchBackup = SearchBackup(repository)
    private val listBackups = ListBackups(repository)
    private val executeBackup = ExecuteBackup()

    @POST
    fun create(request: CreateBackupRequest): Response {
        val command = BackupCommand(
            name = request.name, description = request.description, storageService = request.storageService,
            sourceDir = path(request.sourceDir), destinationDir = path(request.destinationDir),
            username = request.username, password = request.password, nBackupsMax = request.nBackupsMax,
            swSensorMQTT = request.swSensorMQTT)

        val backupId = createBackup.execute(command)

        return Response
            .status(Response.Status.CREATED)
            .entity(mapOf("id" to backupId.value))
            .build()
    }

    @GET
    fun list(): Response {
        val backups = listBackups.execute().stream().map(BackupResponse::from)
        return Response.ok(backups).build()
    }

    @GET
    @Path("/{backupID}")
    fun getBackup(@PathParam("backupID") id: String): Response {
        val backup = searchBackup.execute(BackupId(id))
        return backup?.let { Response.ok(BackupResponse.from(it)).build() }
            ?: Response.status(Response.Status.NOT_FOUND).build()
    }

    @GET
    @Path("/{backupID}/execute")
    suspend fun executeBackup(@PathParam("backupID") id: String): Response {
        val backup = searchBackup.execute(BackupId(id))
        return backup?.let { backup ->
            ssRegistry.getRepository(backup.storageService)?.let {
                executeBackup.execute(backup, it)
                Response.status(Response.Status.CREATED).build()
            } ?: Response.status(Response.Status.BAD_REQUEST)
                    .entity("Storage service '${backup.storageService}' not supported")
                    .build()
        } ?: Response.status(Response.Status.NOT_FOUND).build()
    }

    private fun path(path: String): java.nio.file.Path {
        return java.nio.file.Path.of(path)
    }
}
