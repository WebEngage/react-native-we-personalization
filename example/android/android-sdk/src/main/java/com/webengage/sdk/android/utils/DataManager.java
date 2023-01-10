package com.webengage.sdk.android.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public abstract class DataManager extends SQLiteOpenHelper {


    public DataManager(Context context, String DATABASE_NAME, int DATABASE_VERSION) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.onDatabaseCreate(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        this.onDatabaseUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onDatabaseDowngrade(db, oldVersion, newVersion);
    }

    protected SQLiteDatabase _getReadableDatabase() {
        int i;
        for (i = 1; i <= 3; i++) {
            try {
                return getReadableDatabase();
            } catch (Exception e) {

            }
        }
        return null;
    }

    protected SQLiteDatabase _getWritableDatabase() {
        int i;
        for (i = 1; i <= 3; i++) {
            try {
                return getWritableDatabase();
            } catch (Exception e) {

            }
        }
        return null;
    }

    public long insert(String tableName, ContentValues contentValues) {
        int i;
        for (i = 1; i <= 3; i++) {
            SQLiteDatabase db = this._getWritableDatabase();
            try {
                return db.insertOrThrow(tableName, null, contentValues);
            } catch (Exception e) {

            }
            /* Do not close the the db since all child classes are singleton.
            finally {
                if (db != null) {
                   db.close();
                }
            }*/
        }
        return -1;
    }

    public int update(String tableName, ContentValues contentValues, String selection, String[] selectionArgs) {
        int i;
        for (i = 1; i <= 3; i++) {
            try {
                return this._getWritableDatabase().update(tableName, contentValues, selection, selectionArgs);
            } catch (Exception e) {

            }
        }
        return -1;
    }

    public int deleteRow(String tableName, String selection, String[] selectionArgs) {
        int i;
        for (i = 1; i <= 3; i++) {
            try {
                return this._getWritableDatabase().delete(tableName, selection, selectionArgs);
            } catch (Exception e) {

            }
        }
        return -1;
    }

    public Cursor readableRawQuery(String sql, String[] selectionArgs) {
        int i;
        for (i = 1; i <= 3; i++) {
            try {
                return this._getReadableDatabase().rawQuery(sql, selectionArgs);
            } catch (Exception e) {

            }
        }
        return null;
    }

    public long getDatabaseSize() {
        SQLiteDatabase db = this._getReadableDatabase();
        try {
            File file = new File(db.getPath());
            return file.length();
        } catch (Exception e) {

        }
        return 0;
    }

    public abstract void onDatabaseCreate(SQLiteDatabase db);

    public abstract void onDatabaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public abstract void onDatabaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public abstract int getOldestData();

    public abstract void deleteRow(int id);

}
