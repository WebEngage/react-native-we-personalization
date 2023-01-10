package com.webengage.sdk.android.utils.http;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.webengage.sdk.android.utils.DataManager;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpDataManager extends DataManager {
    private static final String DATABASE_NAME = "http_data.db";
    private static final int DATABASE_VERSION = 3;

    private static final String CACHE_FOLDER = "we_http_cache";
    private static final String HTTP_CACHE_TABLE = "cache_table";
    private static final String URL_ID = "id";
    private static final String URL = "url";
    private static final String TIMESTAMP = "timestamp";
    private static final String HEADERS = "headers";
    private static final String FILE = "file";
    private static final String FILE_SIZE = "file_size";
    private static final String FLAGS = "flags";
    private static final int MAX_DB_SIZE = 20 * 1024 * 1024;

    private Context context;
    private static HttpDataManager instance = null;

    private HttpDataManager(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
        this.context = context;
    }

    public static HttpDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpDataManager.class) {
                if (instance == null) {
                    instance = new HttpDataManager(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onDatabaseCreate(SQLiteDatabase db) {
        String CREATE_HTTP_CACHE_TABLE = String.format("CREATE TABLE %S (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s BLOB, %s TEXT, %s REAL, %s INTEGER)",
                HTTP_CACHE_TABLE, URL_ID, URL, TIMESTAMP, HEADERS, FILE, FILE_SIZE, FLAGS);
        db.execSQL(CREATE_HTTP_CACHE_TABLE);
    }

    @Override
    public void onDatabaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", HTTP_CACHE_TABLE));
        this.onDatabaseCreate(db);
        try {
            deleteDir(getWebEngageCacheDir());
        } catch (Exception e) {

        }
    }

    @Override
    public void onDatabaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", HTTP_CACHE_TABLE));
        this.onDatabaseCreate(db);
        try {
            deleteDir(getWebEngageCacheDir());
        } catch (Exception e) {

        }
    }

    @Override
    public int getOldestData() {
        String query = "SELECT " + URL_ID + " FROM " + HTTP_CACHE_TABLE + " ORDER BY " + TIMESTAMP + " ASC LIMIT 0,1";
        Cursor cursor = readableRawQuery(query, null);
        int id = -1;
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return id;
    }

    public Response getCache(String HASHED_URL) {
        Cursor c = readableRawQuery("select * from " + HTTP_CACHE_TABLE
                + " where url=\"" + HASHED_URL + "\"", null);
        Response.Builder builder = new Response.Builder();
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    //Map<String, Object> map = new HashMap<String, Object>();
                    builder.setID(c.getInt(0));
                    builder.setCacheKey(c.getString(c.getColumnIndex(URL)));

                    String filename = c.getString(c.getColumnIndex(FILE));
                    builder.setInputStream(new FileInputStream(getWebEngageCacheDir().getAbsolutePath() + "/" + filename));

                    //builder.setInputStream(new ByteArrayInputStream(c.getBlob(c.getColumnIndex(FILE))));
                    builder.setException(null);
                    builder.setModifiedState(false);
                    builder.setResponseCode(-1);
                    builder.setResponseHeaders(deserializeMap(c.getBlob(c.getColumnIndex(HEADERS))));
                    builder.setTimeStamp(Long.valueOf(c.getString(c.getColumnIndex(TIMESTAMP))));
                    return builder.build();
                }
            } catch (Exception e) {

            } finally {
                if (!c.isClosed()) {
                    c.close();
                }
            }
        }
        builder.setCacheKey(HASHED_URL);
        builder.setException(new FileNotFoundException("Unable to find file in cache : " + HASHED_URL));
        builder.setModifiedState(false);
        return builder.build();

    }

    public Set<String> getAllCachedResources() {
        Set<String> resources = new HashSet<String>();
        String query = "select " + URL + " from " + HTTP_CACHE_TABLE + " where " + FLAGS + " & " + RequestObject.FLAG_PERSIST_AFTER_CONFIG_REFRESH + " != " + RequestObject.FLAG_PERSIST_AFTER_CONFIG_REFRESH;
        Cursor c = readableRawQuery(query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String url = c.getString(c.getColumnIndex(URL));
                    resources.add(url);
                } while (c.moveToNext());
            }
            c.close();
        }
        return resources;
    }

    public int removeResourcesByURL(Set<String> urls) {
        if (urls == null || urls.size() == 0) {
            return 0;
        }
        Set<String> removedUrls = new HashSet<String>();
        for (String url : urls) {
            if (deleteFile(String.valueOf(url.hashCode()))) {
                removedUrls.add(url);
            }
        }

        if (removedUrls.size() > 0) {
            return deleteRow(HTTP_CACHE_TABLE, URL + " in (" + new String(new char[removedUrls.size() - 1]).replaceAll("\0", "?,") + "?)", removedUrls.toArray(new String[removedUrls.size()]));
        } else {
            return 0;
        }
    }

    private long getCacheSize() {
        Cursor c = readableRawQuery("SELECT SUM(" + FILE_SIZE + ") FROM " + HTTP_CACHE_TABLE, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(0);
                }
                return 0;
            } catch (Exception e) {

            } finally {
                if (!c.isClosed()) {
                    c.close();
                }
            }
        }
        return 0;
    }

    public byte[] saveHttpData(Response response) {
        if (getDatabaseSize() + getCacheSize() > MAX_DB_SIZE) {
            int oldestDataId = getOldestData();
            if (oldestDataId != -1) {
                deleteRow(oldestDataId);
            }
        }
        long rows = -1;

        String url = response.getCacheKey();

        String filename = String.valueOf(url.hashCode());

        byte[] data = serialize(response.getInputStream());
        if (saveFile(filename, data)) {
            ContentValues values = new ContentValues();
            values.put(URL, url);
            values.put(TIMESTAMP, String.valueOf(response.getTimeStamp()));
            values.put(HEADERS, serializeMap(response.getResponseHeaders()));
            values.put(FILE, filename);
            long size = 0;
            if (data != null) {
                size = data.length;
            }
            values.put(FILE_SIZE, size);
            values.put(FLAGS, response.getFlags());

            rows = update(HTTP_CACHE_TABLE, values, URL + " = ?", new String[]{response.getCacheKey()});
            if (rows <= 0) {
                rows = insert(HTTP_CACHE_TABLE, values);
            }
        }

        return data;
    }

    private File getWebEngageCacheDir() throws Exception {
        File dir = getWebEngageCacheDir(context);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    private static File getWebEngageCacheDir(Context context) {
        return new File(context.getFilesDir(), CACHE_FOLDER);
    }

    private boolean saveFile(String filename, byte[] bytes) {
        if (bytes != null) {
            FileOutputStream fos = null;
            try {
                File file = new File(getWebEngageCacheDir(), filename);
                if (file.exists()) {
                    file.delete();
                }

                fos = new FileOutputStream(file);
                fos.write(bytes);
                return true;
            } catch (Exception e) {

            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {

                    }
                }
            }
        }
        return false;
    }


    @Override
    public void deleteRow(int id) {
        if (deleteFile(id)) {
            deleteRow(HTTP_CACHE_TABLE, URL_ID + "= ?", new String[]{Integer.toString(id)});
        }
    }

    private boolean deleteFile(int id) {
        Cursor c = readableRawQuery("SELECT " + FILE + " FROM " + HTTP_CACHE_TABLE + " WHERE " + URL_ID + "=" + id, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String filename = c.getString(c.getColumnIndex(FILE));
                    return deleteFile(filename);
                }
            } catch (Exception e) {

            } finally {
                if (!c.isClosed()) {
                    c.close();
                }
            }
        }
        return false;
    }

    private boolean deleteFile(String filename) {
        if (!WebEngageUtils.isBlank(filename)) {
            try {
                File file = new File(getWebEngageCacheDir(), filename);
                return file.delete();
            } catch (Exception e) {

            }
        }
        return false;
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private byte[] serializeMap(Map<String, List<String>> map) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);
            objectOutputStream.writeObject(map);
            byte[] ar = baos.toByteArray();
            objectOutputStream.close();
            return ar;
        } catch (IOException e) {
            return null;
        }
    }


    private byte[] serialize(InputStream inputStream) {
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {

            }
        }
    }

    private Map<String, List<String>> deserializeMap(byte[] data) {
        if (data == null) {
            return null;
        }
        ObjectInputStream objectInputStream = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            return (Map<String, List<String>>) object;

        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (Exception e) {

            }
        }
    }

    public static boolean deleteDatabase(Context context) {
        try {
            return context.deleteDatabase(DATABASE_NAME) && deleteDir(getWebEngageCacheDir(context));
        } catch (Exception e) {

        }
        return false;
    }
}
