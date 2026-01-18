package com.github.taixmiguel.qbs.driven.storage.mega

import com.github.taixmiguel.qbs.application.port.storage.StorageRepository
import com.github.taixmiguel.qbs.driven.storage.mega.configuration.Properties
import dev.carlsen.mega.Mega
import dev.carlsen.mega.model.Node
import dev.carlsen.mega.util.CancellationToken
import dev.carlsen.mega.util.ProgressCountingSink
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import org.eclipse.microprofile.config.Config
import java.io.File
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.jvm.optionals.getOrElse

@ApplicationScoped
class MegaStorageRepository: StorageRepository {
    @Inject
    private lateinit var config: Config

    @Inject
    private lateinit var mega: Mega

    override suspend fun push(pathToUpload: Path, file: File) {
        try {
            login()

            val node = findNode(pathToUpload, true)
            val fileToUpload = kotlinx.io.files.Path(file.absolutePath)

            SystemFileSystem.source(fileToUpload).use { fileSource ->
                mega.uploadFile(
                    destNode = node!!,
                    name = file.name,
                    fileSize = file.length(),
                    fileInputSource = fileSource.buffered(),
                    cancellationToken = CancellationToken.default()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            logout()
        }
    }

    override suspend fun pull(path: Path, filename: String): File? {
        try {
            login()

            val node = findNode(path, false) ?: return null
            val file = mega.getChildren(node).firstOrNull { it.name == filename } ?: return null

            val tempFile = withContext(Dispatchers.IO) {
                File.createTempFile("qbs-mega-", "-download")
            }.apply { deleteOnExit() }
            val fileToDownload = kotlinx.io.files.Path(tempFile.absolutePath)

            withContext(Dispatchers.IO) {
                SystemFileSystem.sink(fileToDownload).use { fileOutputSink ->
                    mega.downloadFile(
                        src = file,
                        fileOutputSink = ProgressCountingSink(
                            delegate = fileOutputSink,
                            totalBytes = file.size,
                            onProgress = { b, t ->
                                println("Downloaded $b of $t bytes")
                            }
                        ).buffered(),
                        cancellationToken = CancellationToken.default()
                    )
                }
            }
            return tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            logout()
        }
    }

    override suspend fun remove(path: Path, filename: String) {
        try {
            login()

            val node = findNode(path, false) ?: return
            val file = mega.getChildren(node).firstOrNull { it.name == filename } ?: return
            mega.delete(file, destroy = true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            logout()
        }
    }

    private suspend fun login() {
        val password = config.getValue<String>(Properties.MEGA_PASSWORD, String::class.java)
        val email = config.getValue<String>(Properties.MEGA_EMAIL, String::class.java)
        mega.login(email, password)
    }

    private suspend fun logout() {
        mega.logout()
    }

    private suspend fun findNode(path: Path, swCreate: Boolean = false): Node? {
        val rootNode: Node = mega.getFileSystem().root ?: Node()
        return findNode(rootNode, path, swCreate)
    }

    private suspend fun findNode(rootNode: Node, path: Path, swCreate: Boolean = false): Node? {
        val route = path.toString()
        val folder = if (route.startsWith("/")) {
            val index = route.indexOf("/", 1)
            route.substring(1, if (index < 0) route.length else index)
        } else route.substring(0, route.indexOf("/"))

        val node = rootNode.getChildren().stream()
            .filter { it.name == folder }
            .findFirst()
            .getOrElse {
                if (swCreate) mega.createDir(folder, rootNode)
                else null
            }

        if (node != null) {
            val nextPath = Path.of(route.substring(route.indexOf(folder) + folder.length))
            return if (nextPath.name.isNotEmpty()) findNode(node, nextPath, swCreate) else node
        }
        return node
    }
}