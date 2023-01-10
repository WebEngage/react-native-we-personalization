package com.webengage.personalization.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

abstract class DataManager (
    context: Context,
    name: String,
    version: Int
) : SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(db: SQLiteDatabase) {
        onDatabaseCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDatabaseUpgrade(db, oldVersion, newVersion)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onDowngrade(db, oldVersion, newVersion)
    }

    protected open fun _getReadableDatabase(): SQLiteDatabase? {
        var i: Int
        i = 1
        while (i <= 3) {
            try {
                return readableDatabase
            } catch (e: Exception) {
            }
            i++
        }
        return null
    }

    protected open fun _getWritableDatabase(): SQLiteDatabase? {
        var i: Int
        i = 1
        while (i <= 3) {
            try {
                return writableDatabase
            } catch (e: Exception) {
            }
            i++
        }
        return null
    }

    open fun insert(tableName: String?, contentValues: ContentValues?): Long {
        var i: Int
        i = 1
        while (i <= 3) {
            val db = _getWritableDatabase()
            try {
                return db!!.insertOrThrow(tableName, null, contentValues)
            } catch (e: Exception) {
            }
            i++
        }
        return -1
    }

    open fun update(
        tableName: String?,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        var i: Int
        i = 1
        while (i <= 3) {
            try {
                return _getWritableDatabase()!!
                    .update(tableName, contentValues, selection, selectionArgs)
            } catch (e: Exception) {
            }
            i++
        }
        return -1
    }

    open fun deleteRow(
        tableName: String?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        var i: Int
        i = 1
        while (i <= 3) {
            try {
                return _getWritableDatabase()!!.delete(tableName, selection, selectionArgs)
            } catch (e: Exception) {
            }
            i++
        }
        return -1
    }

    open fun readableRawQuery(sql: String?, selectionArgs: Array<String?>?): Cursor? {
        var i: Int
        i = 1
        while (i <= 3) {
            try {
                return _getReadableDatabase()!!.rawQuery(sql, selectionArgs)
            } catch (e: Exception) {
            }
            i++
        }
        return null
    }

    open fun getDatabaseSize(): Long {
        val db = _getReadableDatabase()
        try {
            val file = File(db!!.path)
            return file.length()
        } catch (e: Exception) {
        }
        return 0
    }

    abstract fun onDatabaseCreate(db: SQLiteDatabase)

    abstract fun onDatabaseUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)

    abstract fun onDatabaseDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)

    abstract fun getOldestData(): Int

    abstract fun deleteRow(id: Int)
}