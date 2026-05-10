package cn.yajienet.huanaer.util

import android.content.Context
import android.os.Build
import cn.yajienet.huanaer.data.local.database.HuaNaErDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class BackupResult(
    val success: Boolean,
    val filePath: String? = null,
    val error: String? = null
)

class BackupManager(private val context: Context) {

    companion object {
        private const val BACKUP_FOLDER = "HuaNaEr_Backup"
        private const val DATABASE_NAME = "huanaer_database"
    }

    suspend fun createBackup(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val backupDir = getBackupDirectory()
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
            val backupFile = File(backupDir, "huanaer_backup_$timestamp.zip")

            ZipOutputStream(FileOutputStream(backupFile)).use { zipOut ->
                // 备份主数据库文件
                val dbFile = context.getDatabasePath(DATABASE_NAME)
                if (dbFile.exists()) {
                    addFileToZip(zipOut, dbFile, DATABASE_NAME)
                }

                // 备份 WAL 文件
                val walFile = File(dbFile.parent, "$DATABASE_NAME-wal")
                if (walFile.exists()) {
                    addFileToZip(zipOut, walFile, "$DATABASE_NAME-wal")
                }

                // 备份 SHM 文件
                val shmFile = File(dbFile.parent, "$DATABASE_NAME-shm")
                if (shmFile.exists()) {
                    addFileToZip(zipOut, shmFile, "$DATABASE_NAME-shm")
                }

                // 备份设置文件
                val settingsDir = File(context.filesDir, "datastore")
                if (settingsDir.exists()) {
                    settingsDir.listFiles()?.forEach { file ->
                        if (file.name.endsWith(".preferences_pb")) {
                            addFileToZip(zipOut, file, "datastore/${file.name}")
                        }
                    }
                }
            }

            BackupResult(success = true, filePath = backupFile.absolutePath)
        } catch (e: Exception) {
            BackupResult(success = false, error = e.message ?: "备份失败")
        }
    }

    suspend fun restoreFromBackup(backupFile: File): BackupResult = withContext(Dispatchers.IO) {
        try {
            // 先删除现有数据库文件
            deleteDatabaseFiles()

            // 解压备份文件
            ZipInputStream(FileInputStream(backupFile)).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    restoreFileFromZip(zipIn, entry.name)
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            BackupResult(success = true)
        } catch (e: Exception) {
            BackupResult(success = false, error = e.message ?: "恢复失败")
        }
    }

    suspend fun clearAllData(database: HuaNaErDatabase): Boolean = withContext(Dispatchers.IO) {
        try {
            // 清除数据库所有表
            database.clearAllTables()
            // 关闭数据库
            database.close()
            // 删除数据库文件
            deleteDatabaseFiles()
            // 清除设置
            clearSettings()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun deleteDatabaseFiles() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (dbFile.exists()) {
            dbFile.delete()
        }
        File(dbFile.parent, "$DATABASE_NAME-wal").delete()
        File(dbFile.parent, "$DATABASE_NAME-shm").delete()
    }

    private fun clearSettings() {
        val settingsDir = File(context.filesDir, "datastore")
        if (settingsDir.exists()) {
            settingsDir.deleteRecursively()
        }
    }

    private fun getBackupDirectory(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(null), BACKUP_FOLDER)
        } else {
            File(context.filesDir, BACKUP_FOLDER)
        }
    }

    private fun addFileToZip(zipOut: ZipOutputStream, file: File, entryName: String) {
        val entry = ZipEntry(entryName)
        zipOut.putNextEntry(entry)
        FileInputStream(file).use { input ->
            input.copyTo(zipOut)
        }
        zipOut.closeEntry()
    }

    private fun restoreFileFromZip(zipIn: ZipInputStream, entryName: String) {
        val dbPath = context.getDatabasePath(DATABASE_NAME).parentFile!!

        when {
            entryName == DATABASE_NAME -> {
                val dbFile = context.getDatabasePath(DATABASE_NAME)
                FileOutputStream(dbFile).use { output ->
                    zipIn.copyTo(output)
                }
            }
            entryName == "$DATABASE_NAME-wal" -> {
                val walFile = File(dbPath, "$DATABASE_NAME-wal")
                FileOutputStream(walFile).use { output ->
                    zipIn.copyTo(output)
                }
            }
            entryName == "$DATABASE_NAME-shm" -> {
                val shmFile = File(dbPath, "$DATABASE_NAME-shm")
                FileOutputStream(shmFile).use { output ->
                    zipIn.copyTo(output)
                }
            }
            entryName.startsWith("datastore/") -> {
                val settingsDir = File(context.filesDir, "datastore")
                if (!settingsDir.exists()) {
                    settingsDir.mkdirs()
                }
                val fileName = entryName.substringAfter("datastore/")
                val settingsFile = File(settingsDir, fileName)
                FileOutputStream(settingsFile).use { output ->
                    zipIn.copyTo(output)
                }
            }
        }
    }

    fun getBackupFiles(): List<File> {
        val backupDir = getBackupDirectory()
        if (!backupDir.exists()) return emptyList()
        return backupDir.listFiles()
            ?.filter { it.extension == "zip" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }
}