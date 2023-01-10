package com.webengage.personalization.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import com.webengage.personalization.data.network.RequestObject
import com.webengage.personalization.data.network.Response

import java.io.*
import java.util.*

class ImageCacheManager private constructor(
    val context: Context,
    val name: String,
    val version: Int
): DataManager(context, name, version) {

    private val CACHE_FOLDER = "p_image_cache"
    private val IMAGE_CACHE_TABLE = "image_cache_table"
    private val URL_ID = "id"
    private val URL = "url"
    private val TIMESTAMP = "timestamp"
    private val HEADERS = "headers"
    private val FILE = "file"
    private val FILE_SIZE = "file_size"
    private val FLAGS = "flags"
    private val MAX_DB_SIZE = 20 * 1024 * 1024

    companion object {
        private val DATABASE_NAME = "p_image_data.db"
        private val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: ImageCacheManager? = null

        fun getInstance(context: Context): ImageCacheManager {
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildImageCache(context).also { INSTANCE = it }
            }
            return INSTANCE!!
        }

        private fun buildImageCache(context: Context) = ImageCacheManager(context, DATABASE_NAME, DATABASE_VERSION)
    }

    override fun deleteRow(id: Int) {
        if (deleteFile(id)) {
            deleteRow(
                IMAGE_CACHE_TABLE,
                URL_ID + "= ?",
                arrayOf(Integer.toString(id))
            )
        }
    }

    override fun onDatabaseCreate(db: SQLiteDatabase) {
        val CREATE_IMAGE_CACHE_TABLE = String.format(
            "CREATE TABLE %S (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s BLOB, %s TEXT, %s REAL, %s INTEGER)",
            IMAGE_CACHE_TABLE,
            URL_ID,
            URL,
            TIMESTAMP,
            HEADERS,
            FILE,
            FILE_SIZE,
            FLAGS
        )
        db.execSQL(CREATE_IMAGE_CACHE_TABLE)
    }

    override fun onDatabaseUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", IMAGE_CACHE_TABLE))
        onDatabaseCreate(db)
        try {
            deleteDir(getWebEngageCacheDir())
        } catch (e: Exception) {
        }
    }

    override fun onDatabaseDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", IMAGE_CACHE_TABLE))
        onDatabaseCreate(db)
        try {
            deleteDir(getWebEngageCacheDir())
        } catch (e: java.lang.Exception) {
        }
    }

    override fun getOldestData(): Int {
        val query = "SELECT $URL_ID  FROM $IMAGE_CACHE_TABLE ORDER BY $TIMESTAMP  ASC LIMIT 0,1"
        val cursor = readableRawQuery(query, null)
        var id = -1
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return id
    }

    fun getCache(HASHED_URL: String): Response? {
        val c = readableRawQuery(
            "select * from $IMAGE_CACHE_TABLE where url=\"$HASHED_URL\"", null
        )
        val builder = Response.Builder()
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    builder.setID(c.getInt(0))
                    builder.setCacheKey(c.getString(c.getColumnIndex(URL)))
                    val filename = c.getString(c.getColumnIndex(FILE))
                    builder.setInputStream(FileInputStream(getWebEngageCacheDir().absolutePath + "/" + filename))
                    builder.setException(null)
                    builder.setModifiedState(false)
                    builder.setResponseCode(-1)
                    builder.setResponseHeaders(deserializeMap(c.getBlob(c.getColumnIndex(HEADERS))))
                    builder.setTimeStamp(
                        java.lang.Long.valueOf(
                            c.getString(
                                c.getColumnIndex(
                                    TIMESTAMP
                                )
                            )
                        )
                    )
                    return builder.build()
                }
            } catch (e: java.lang.Exception) {
            } finally {
                if (!c.isClosed) {
                    c.close()
                }
            }
        }
        builder.setCacheKey(HASHED_URL)
        builder.setException(FileNotFoundException("Unable to find file in cache : $HASHED_URL"))
        builder.setModifiedState(false)
        return builder.build()
    }

    fun getAllCachedResources(): Set<String>? {
        val resources: MutableSet<String> = HashSet()
        val query =
            "select $URL from $IMAGE_CACHE_TABLE where $FLAGS & ${RequestObject.FLAG_PERSIST_AFTER_CONFIG_REFRESH} " +
                    " !=  ${RequestObject.FLAG_PERSIST_AFTER_CONFIG_REFRESH}"
        val c = readableRawQuery(query, null)
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    val url = c.getString(c.getColumnIndex(URL))
                    resources.add(url)
                } while (c.moveToNext())
            }
            c.close()
        }
        return resources
    }

    fun removeResourcesByURL(urls: Set<String>?): Int {
        if (urls == null || urls.size == 0) {
            return 0
        }
        val removedUrls: MutableSet<String?> = HashSet()
        for (url: String in urls) {
            if (deleteFile(url.hashCode().toString())) {
                removedUrls.add(url)
            }
        }
        return if (removedUrls.size > 0) {
            deleteRow(
                IMAGE_CACHE_TABLE, "$URL in (" + String(
                    CharArray(removedUrls.size - 1)
                ).replace("\u0000".toRegex(), "?,") + "?)", removedUrls.toTypedArray()
            )
        } else {
            0
        }
    }

    private fun getCacheSize(): Long {
        val c = readableRawQuery(
            "SELECT SUM($FILE_SIZE) FROM $IMAGE_CACHE_TABLE",
            null
        )
        if (c != null) {
            try {
                return if (c.moveToFirst()) {
                    c.getInt(0).toLong()
                } else 0
            } catch (e: java.lang.Exception) {
            } finally {
                if (!c.isClosed) {
                    c.close()
                }
            }
        }
        return 0
    }

    fun saveImageData(response: Response): ByteArray? {
        if (getDatabaseSize() + getCacheSize() > MAX_DB_SIZE) {
            val oldestDataId = getOldestData()
            if (oldestDataId != -1) {
                deleteRow(oldestDataId)
            }
        }
        var rows: Long = -1
        val url = response.uRL
        val filename = url.hashCode().toString()
        val data = serialize(response.inputStream!!)
        if (saveFile(filename, data)) {
            val values = ContentValues()
            values.put(URL, url)
            values.put(TIMESTAMP, response.timeStamp.toString())
            values.put(HEADERS, serializeMap(response.responseHeaders!!))
            values.put(FILE, filename)
            var size: Long = 0
            if (data != null) {
                size = data.size.toLong()
            }
            values.put(FILE_SIZE, size)
            values.put(FLAGS, response.flags)
            rows = update(
                IMAGE_CACHE_TABLE,
                values,
                URL + " = ?",
                arrayOf(response.uRL)
            ).toLong()
            if (rows <= 0) {
                rows = insert(IMAGE_CACHE_TABLE, values)
            }
        }
        return data
    }

    @Throws(java.lang.Exception::class)
    private fun getWebEngageCacheDir(): File {
        val dir = getWebEngageCacheDir(context)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }

    private fun getWebEngageCacheDir(context: Context): File {
        return File(context.filesDir, CACHE_FOLDER)
    }

    private fun saveFile(filename: String, bytes: ByteArray?): Boolean {
        if (bytes != null) {
            var fos: FileOutputStream? = null
            try {
                val file = File(getWebEngageCacheDir(), filename)
                if (file.exists()) {
                    file.delete()
                }
                fos = FileOutputStream(file)
                fos.write(bytes)
                return true
            } catch (e: java.lang.Exception) {
            } finally {
                if (fos != null) {
                    try {
                        fos.flush()
                        fos.close()
                    } catch (e: java.lang.Exception) {
                    }
                }
            }
        }
        return false
    }

    private fun deleteFile(id: Int): Boolean {
        val c = readableRawQuery(
            "SELECT $FILE FROM $IMAGE_CACHE_TABLE WHERE $URL_ID=$id",
            null
        )
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    val filename = c.getString(c.getColumnIndex(FILE))
                    return deleteFile(filename)
                }
            } catch (e: java.lang.Exception) {
            } finally {
                if (!c.isClosed) {
                    c.close()
                }
            }
        }
        return false
    }

    private fun deleteFile(filename: String): Boolean {
        if (!TextUtils.isEmpty(filename)) {
            try {
                val file = File(getWebEngageCacheDir(), filename)
                return file.delete()
            } catch (e: java.lang.Exception) {
            }
        }
        return false
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            return dir.delete()
        } else return if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    private fun serializeMap(map: Map<String, List<String>>): ByteArray? {
        try {
            val baos = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(baos)
            objectOutputStream.writeObject(map)
            val ar = baos.toByteArray()
            objectOutputStream.close()
            return ar
        } catch (e: IOException) {
            return null
        }
    }


    private fun serialize(inputStream: InputStream): ByteArray? {
        val buffer = ByteArray(8 * 1024)
        var bytesRead: Int
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }
            return byteArrayOutputStream.toByteArray()
        } catch (e: java.lang.Exception) {
            return null
        } finally {
            try {
                inputStream.close()
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun deserializeMap(data: ByteArray?): Map<String, List<String>>? {
        if (data == null) {
            return null
        }
        var objectInputStream: ObjectInputStream? = null
        try {
            val byteArrayInputStream = ByteArrayInputStream(data)
            objectInputStream = ObjectInputStream(byteArrayInputStream)
            val requestObject = objectInputStream.readObject()
            return if (requestObject is Map<*, *>) {
                requestObject as Map<String, List<String>>?
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            return null
        } finally {
            try {
                objectInputStream?.close()
            } catch (e: java.lang.Exception) {
            }
        }
    }

    fun deleteDatabase(context: Context): Boolean {
        try {
            return context.deleteDatabase(DATABASE_NAME) && deleteDir(
                getWebEngageCacheDir(context)
            )
        } catch (e: java.lang.Exception) {
        }
        return false
    }
}