package com.github.taixmiguel.qbs.driven.storage.mega

import com.github.taixmiguel.qbs.application.port.storage.StorageRepository
import com.github.taixmiguel.qbs.driven.storage.mega.configuration.Properties
import dev.carlsen.mega.Mega
import dev.carlsen.mega.model.Node
import dev.carlsen.mega.util.CancellationToken
import dev.carlsen.mega.util.ProgressCountingSink
import io.quarkus.logging.Log
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
        Log.infof("UPLOAD: Starting [file='%s', path='%s', size=%d bytes]", file.name, pathToUpload, file.length())
        try {
            login()

            val node = findNode(pathToUpload, true)
            if (node == null) {
                Log.errorf("UPLOAD: Failed — could not find or create destination node [path='%s']", pathToUpload)
                return
            }
            Log.debugf(
                "UPLOAD: Destination node found [destNode.name='%s', destNode.hash='%s', fileName='%s', fileSize=%d, fileExists=%b, fileCanRead=%b, filePath='%s']",
                node.name, node.hash, file.name, file.length(), file.exists(), file.canRead(), file.absolutePath
            )

            val fileToUpload = kotlinx.io.files.Path(file.absolutePath)
            Log.debugf("UPLOAD: Uploading file to MEGA...")

            SystemFileSystem.source(fileToUpload).use { fileSource ->
                mega.uploadFile(
                    destNode = node,
                    name = file.name,
                    fileSize = file.length(),
                    fileInputSource = fileSource.buffered(),
                    cancellationToken = CancellationToken.default()
                )
            }
            Log.infof("UPLOAD: Completed successfully [file='%s']", file.name)
        } catch (e: Exception) {
            Log.errorf(e, "UPLOAD: Failed with exception [file='%s', path='%s']", file.name, pathToUpload)
        } finally {
            logout()
        }
    }

    override suspend fun pull(path: Path, filename: String): File? {
        Log.infof("DOWNLOAD: Starting [file='%s', path='%s']", filename, path)
        try {
            login()

            val node = findNode(path, false)
            if (node == null) {
                Log.warnf("DOWNLOAD: Directory node not found [path='%s']", path)
                return null
            }
            Log.debugf("DOWNLOAD: Directory node found [node='%s']", node.name)

            val children = mega.getChildren(node)
            Log.debugf("DOWNLOAD: Found %d children in node", children.size)

            val file = children.firstOrNull { it.name == filename }
            if (file == null) {
                Log.warnf("DOWNLOAD: File not found in remote node [file='%s', node='%s']", filename, node.name)
                return null
            }
            Log.debugf("DOWNLOAD: File located [file='%s', size=%d bytes]", file.name, file.size)

            val tempFile = withContext(Dispatchers.IO) {
                File.createTempFile("qbs-mega-", "-download")
            }.apply { deleteOnExit() }
            Log.debugf("DOWNLOAD: Temp file created [path='%s']", tempFile.absolutePath)

            val fileToDownload = kotlinx.io.files.Path(tempFile.absolutePath)

            withContext(Dispatchers.IO) {
                SystemFileSystem.sink(fileToDownload).use { fileOutputSink ->
                    mega.downloadFile(
                        src = file,
                        fileOutputSink = ProgressCountingSink(
                            delegate = fileOutputSink,
                            totalBytes = file.size,
                            onProgress = { b, t ->
                                Log.infof("DOWNLOAD: Progress [%d / %d bytes]", b, t)
                            }
                        ).buffered(),
                        cancellationToken = CancellationToken.default()
                    )
                }
            }

            Log.infof("DOWNLOAD: Completed successfully [file='%s', tempPath='%s']", filename, tempFile.absolutePath)
            return tempFile
        } catch (e: Exception) {
            Log.errorf(e, "DOWNLOAD: Failed with exception [file='%s', path='%s']", filename, path)
            return null
        } finally {
            logout()
        }
    }

    override suspend fun remove(path: Path, filename: String) {
        Log.infof("CLEANUP: Starting deletion [file='%s', path='%s']", filename, path)
        try {
            login()

            val node = findNode(path, false)
            if (node == null) {
                Log.warnf("CLEANUP: Directory node not found, skipping [path='%s']", path)
                return
            }

            val file = mega.getChildren(node).firstOrNull { it.name == filename }
            if (file == null) {
                Log.warnf("CLEANUP: File not found in remote node, skipping [file='%s', node='%s']", filename, node.name)
                return
            }

            Log.debugf("CLEANUP: Deleting file [file='%s']", file.name)
            mega.delete(file, destroy = true)
            Log.infof("CLEANUP: Deleted successfully [file='%s']", filename)
        } catch (e: Exception) {
            Log.errorf(e, "CLEANUP: Failed with exception [file='%s', path='%s']", filename, path)
        } finally {
            logout()
        }
    }

    private suspend fun login() {
        val email = config.getValue<String>(Properties.MEGA_EMAIL, String::class.java)
        Log.debugf("AUTH: Logging in to MEGA [email='%s']", email)
        mega.login(email, config.getValue<String>(Properties.MEGA_PASSWORD, String::class.java))
        Log.debugf("AUTH: Login successful")
    }

    private suspend fun logout() {
        Log.debugf("AUTH: Logging out from MEGA")
        mega.logout()
        Log.debugf("AUTH: Logout successful")
    }

    private suspend fun findNode(path: Path, swCreate: Boolean = false): Node? {
        val fs = mega.getFileSystem()
        val rootNode: Node = fs.root ?: run {
            Log.errorf("SEARCH: FileSystem root is null after login!")
            return null
        }
        Log.debugf("SEARCH: FileSystem root = '%s', root.hash = '%s'", rootNode.name, rootNode.hash)
        return findNode(rootNode, path, swCreate)
    }

    private suspend fun findNode(rootNode: Node, path: Path, swCreate: Boolean = false): Node? {
        val normPath = if (path.isAbsolute) path.root?.relativize(path) ?: path else path
        if (normPath.nameCount == 0) {
            Log.debugf("SEARCH: Empty path — returning rootNode [node='%s']", rootNode.name)
            return rootNode
        }

        val folder = normPath.getName(0).toString()
        Log.debugf("SEARCH: Looking for node [folder='%s', path='%s', swCreate=%b]", folder, path, swCreate)

        val node = rootNode.getChildren().stream()
            .filter { it.name == folder }
            .findFirst()
            .getOrElse {
                if (swCreate) {
                    Log.infof("SEARCH: Node not found, creating [folder='%s', rootNode.name='%s', rootNode.hash='%s']", folder, rootNode.name, rootNode.hash)
                    try {
                        val created = mega.createDir(folder, rootNode)
                        Log.infof("SEARCH: Node created successfully [folder='%s', newNode.hash='%s']", folder, created.hash)
                        created
                    } catch (e: Exception) {
                        Log.errorf(e, "SEARCH: createDir failed [folder='%s', rootNode.name='%s']", folder, rootNode.name)
                        null
                    }
                } else {
                    Log.warnf("SEARCH: Node not found and creation disabled [folder='%s']", folder)
                    null
                }
            }

        if (node != null) {
            return if (normPath.nameCount > 1) {
                val nextPath = normPath.subpath(1, normPath.nameCount)
                Log.debugf("SEARCH: Descending into next path segment [next='%s']", nextPath)
                findNode(node, nextPath, swCreate)
            } else {
                Log.debugf("SEARCH: Node resolved successfully [node='%s']", node.name)
                node
            }
        }
        return null
    }
}