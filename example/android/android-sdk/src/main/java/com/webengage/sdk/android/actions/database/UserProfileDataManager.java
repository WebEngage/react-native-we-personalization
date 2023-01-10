package com.webengage.sdk.android.actions.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.webengage.sdk.android.utils.DataManager;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserProfileDataManager extends DataManager implements OnDataHolderChangeListener {
    private static final String DATABASE_NAME = "user_data.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_USER = "user";
    private static final String USER_ATTRIBUTE_ID = "id";
    private static final String USER_IDENTIFIER = "cuid";
    private static final String USER_ATTRIBUTE_NAME = "user_attribute_name";
    private static final String USER_ATTRIBUTE_VALUE = "user_attribute_value";
    private static final String USER_ATTRIBUTE_OPERATION = "operation";
    private static final String USER_ATTRIBUTE_DATA_TYPE = "use_attribute_data_type";
    private static final String USER_ATTRIBUTE_CONTAINER = "user_attribute_container";
    private static UserProfileDataManager instance = null;


    private UserProfileDataManager(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    public static UserProfileDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserProfileDataManager.class) {
                if (instance == null) {
                    instance = new UserProfileDataManager(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onDatabaseCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = String.format("CREATE TABLE %S (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT, %s TEXT, %s BLOB,%s TEXT,%s TEXT,%s TEXT)",
                TABLE_USER, USER_ATTRIBUTE_ID, USER_IDENTIFIER, USER_ATTRIBUTE_NAME, USER_ATTRIBUTE_VALUE, USER_ATTRIBUTE_OPERATION, USER_ATTRIBUTE_DATA_TYPE, USER_ATTRIBUTE_CONTAINER);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onDatabaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " LIMIT 0", null);
            if (cursor.getColumnIndex(USER_ATTRIBUTE_CONTAINER) == -1) {
                db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_USER));
                this.onDatabaseCreate(db);
            }
        } catch (SQLException e) {
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_USER));
            this.onDatabaseCreate(db);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + USER_ATTRIBUTE_CONTAINER + " =\"" + DataContainer.ANDROID.toString() + "\"", null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String key = cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_NAME));
                        if (key != null) {
                            if (key.endsWith(WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW) || key.endsWith(WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLICK) || key.endsWith(WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW_SESSION) || key.endsWith(WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLOSE_SESSION)) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(USER_ATTRIBUTE_CONTAINER, DataContainer.SCOPES.toString());
                                db.update(TABLE_USER, contentValues, USER_ATTRIBUTE_ID + " = ?", new String[]{Integer.toString(cursor.getInt(cursor.getColumnIndex(USER_ATTRIBUTE_ID)))});
                            }
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


    }

    @Override
    public void onDatabaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_USER));
        this.onDatabaseCreate(db);
    }


    private void deleteUserAttribute(String cuid, String attributeKey, DataContainer dataContainer) {
        deleteRow(TABLE_USER, USER_ATTRIBUTE_NAME + " = ? AND " + USER_IDENTIFIER + " = ? AND " + USER_ATTRIBUTE_CONTAINER + " = ?", new String[]{attributeKey, cuid, dataContainer.toString()});
    }

    private void updateUserAttribute(String cuid, String attributeKey, Object value, DataContainer container, Operation operation) {
        long rows;
        ContentValues values = new ContentValues();
        values.put(USER_IDENTIFIER, cuid);
        values.put(USER_ATTRIBUTE_NAME, attributeKey);
        values.put(USER_ATTRIBUTE_VALUE, WebEngageUtils.serialize(value));
        values.put(USER_ATTRIBUTE_OPERATION, operation.toString());
        DataType dataType = DataType.detect(value);
        if (dataType != null) {
            values.put(USER_ATTRIBUTE_DATA_TYPE, dataType.toString());
        } else {
            return;
        }
        values.put(USER_ATTRIBUTE_CONTAINER, container.toString());
        rows = update(TABLE_USER, values, USER_ATTRIBUTE_NAME + " = ? AND " + USER_IDENTIFIER + " = ? AND " + USER_ATTRIBUTE_CONTAINER + " = ?", new String[]{attributeKey, cuid, container.toString()});
        if (rows <= 0) {
            rows = insert(TABLE_USER, values);
        }
    }

    /**
     * Get yourself a coffee or a beer before proceeding ahead.
     *
     * @param cuid
     * @param luid
     */
    void linkLUIDToCUID(String cuid, String luid) {
        int rows = -1;
        List<String> idsToDelete = new ArrayList<String>();
        Cursor cursor = readableRawQuery("select * from user where cuid =\"" + cuid + "\" and user_attribute_name in(select user_attribute_name from user where cuid =\"" + luid + "\")", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Cursor currentCursor = readableRawQuery("select * from user where cuid =\"" + luid + "\" and user_attribute_name =\"" + cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_NAME)) + "\" and user_attribute_container =\"" + cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_CONTAINER)) + "\"", null);
                    if (currentCursor != null) {
                        if (currentCursor.moveToFirst()) {
                            if (currentCursor.getString(currentCursor.getColumnIndex(USER_ATTRIBUTE_OPERATION)).equalsIgnoreCase(Operation.INCREMENT.toString())) {
                                byte[] value = currentCursor.getBlob(currentCursor.getColumnIndex(USER_ATTRIBUTE_VALUE));
                                Object data = WebEngageUtils.deserialize(value);
                                if (data != null) {
                                    DataType dataType = DataType.valueByString(cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_DATA_TYPE)));
                                    if (DataType.isNumber(dataType)) {
                                        Number newValue = (Number) data;
                                        value = cursor.getBlob(cursor.getColumnIndex(USER_ATTRIBUTE_VALUE));
                                        Object d = WebEngageUtils.deserialize(value);
                                        if (d != null) {
                                            newValue = newValue.doubleValue() + ((Number) d).doubleValue();
                                        }
                                        ContentValues contentValues = new ContentValues();
                                        try {
                                            contentValues.put(USER_ATTRIBUTE_VALUE, WebEngageUtils.serialize(DataType.convert(newValue, DataType.detect(d), false)));
                                        } catch (Exception e) {
                                            contentValues.put(USER_ATTRIBUTE_VALUE, WebEngageUtils.serialize(newValue));
                                        }
                                        update(TABLE_USER, contentValues, USER_ATTRIBUTE_ID + " = ?", new String[]{Integer.toString(currentCursor.getInt(currentCursor.getColumnIndex(USER_ATTRIBUTE_ID)))});
                                    }
                                }

                            }
                            idsToDelete.add(String.valueOf(cursor.getInt(cursor.getColumnIndex(USER_ATTRIBUTE_ID))));
                        }

                        currentCursor.close();

                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (idsToDelete.size() > 0) {
            deleteRow(TABLE_USER, USER_ATTRIBUTE_ID + " in (" + new String(new char[idsToDelete.size() - 1]).replaceAll("\0", "?,") + "?)", idsToDelete.toArray(new String[idsToDelete.size()]));
        }

        ContentValues values = new ContentValues();
        values.put(USER_IDENTIFIER, cuid);

        update(TABLE_USER, values, USER_IDENTIFIER + " = ?", new String[]{luid});


    }

    @Override
    public int getOldestData() {
        return 0;
    }

    @Override
    public void deleteRow(int id) {
        deleteRow(TABLE_USER, USER_ATTRIBUTE_ID + " = ?", new String[]{Integer.toString(id)});
    }


    Object getUserAttribute(String cuid, String key, String category) {
        String query = "select " + USER_ATTRIBUTE_VALUE + " from " + TABLE_USER + " where " + USER_IDENTIFIER + " =\"" + cuid + "\" and " + USER_ATTRIBUTE_CONTAINER + " =\"" + category + "\"";
        Cursor cursor = readableRawQuery(query, null);
        Object data = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                data = WebEngageUtils.deserialize(cursor.getBlob(cursor.getColumnIndex(USER_ATTRIBUTE_VALUE)));
            }
            cursor.close();
        }
        return data;
    }


    @Override
    public void onChange(List<Object> key, Object value, String userIdentifier, Operation operation) {
        if (key == null || key.size() <= 1 || userIdentifier == null || userIdentifier.isEmpty()) {
            return;
        }

        DataContainer container = DataContainer.valueByString(key.get(0).toString());
        if (container != null && container.canBeStored()) {

            if (operation == null) {
                operation = Operation.FORCE_UPDATE;
            }
            String attributeKey = key.get(1).toString();
            switch (operation) {
                case FORCE_UPDATE:
                    if (value != null) {
                        updateUserAttribute(userIdentifier, attributeKey, value, container, Operation.FORCE_UPDATE);
                    } else {
                        deleteUserAttribute(userIdentifier, attributeKey, container);
                    }
                    break;
                case INCREMENT:
                    if (value != null) {
                        updateUserAttribute(userIdentifier, attributeKey, value, container, Operation.INCREMENT);
                    }

                    break;
            }

        }


    }

    public Map<String, Object> getAllUserData(String identifier) {
        Map<String, Object> allUserData = new HashMap<String, Object>();
        for (DataContainer dataContainer : DataContainer.values()) {
            if (dataContainer.canBeStored()) {
                allUserData.put(dataContainer.toString(), new HashMap<String, Object>());
            }
        }
        String query = "SELECT  * FROM " + TABLE_USER + " WHERE " + USER_IDENTIFIER + " = \"" + identifier + "\"";
        Cursor cursor = readableRawQuery(query, null);
        String key;
        String type;
        byte[] value;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    key = cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_NAME));
                    type = cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_CONTAINER));
                    value = cursor.getBlob(cursor.getColumnIndex(USER_ATTRIBUTE_VALUE));
                    Object data = WebEngageUtils.deserialize(value);
                    if (data != null) {
                        DataContainer dataContainer = DataContainer.valueByString(type);
                        if (dataContainer != null) {
                            if (allUserData.get(dataContainer.toString()) != null) {
                                ((Map<String, Object>) allUserData.get(dataContainer.toString())).put(key, data);
                            }

                        }
                    }
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }

        return allUserData;
    }


    public Map<String, Set<String>> getAllEventCriteriaIdsAcrossUsers() {
        String query = "SELECT  * FROM " + TABLE_USER + " WHERE " + USER_ATTRIBUTE_CONTAINER + " = \"" + DataContainer.EVENT_CRITERIA.toString() + "\"";
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        Cursor cursor = readableRawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String criteriaId = cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_NAME));
                    String userId = cursor.getString(cursor.getColumnIndex(USER_IDENTIFIER));
                    if (map.get(userId) == null) {
                        map.put(userId, new HashSet<String>());
                    }
                    map.get(userId).add(criteriaId);
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }

        return map;
    }

    public Map<String, Set<String>> getAllScopesAcrossUser() {
        String query = "SELECT  * FROM " + TABLE_USER + " WHERE " + USER_ATTRIBUTE_CONTAINER + " = \"" + DataContainer.SCOPES.toString() + "\"";
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        Cursor cursor = readableRawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String scopeKey = cursor.getString(cursor.getColumnIndex(USER_ATTRIBUTE_NAME));
                    String userId = cursor.getString(cursor.getColumnIndex(USER_IDENTIFIER));
                    if (map.get(userId) == null) {
                        map.put(userId, new HashSet<String>());
                    }
                    map.get(userId).add(scopeKey);
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }

        return map;
    }

    public static boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
