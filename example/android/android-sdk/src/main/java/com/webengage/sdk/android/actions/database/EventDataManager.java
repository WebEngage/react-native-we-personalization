package com.webengage.sdk.android.actions.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.utils.DataManager;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDataManager extends DataManager {
    private static final String DATABASE_NAME = "event_data.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_EVENTS = "events";
    private static final String EVENT_ID = "id";
    private static final String LICENSE_CODE = "license_code";
    private static final String INTERFACE_ID = "interface_id";
    private static final String LUID = "luid";
    private static final String SUID = "suid";
    private static final String CUID = "cuid";
    private static final String CATEGORY = "category";
    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_TIME = "event_time";
    private static final String EVENT_DATA = "event_data";
    private static final String SYSTEM_DATA = "system_data";
    private static final String SYNC_STATE = "sync_state";
    private static final int MAX_DB_SIZE = 3 * 1024 * 1024;
    private static EventDataManager instance = null;

    private EventDataManager(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    public static EventDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (EventDataManager.class) {
                if (instance == null) {
                    instance = new EventDataManager(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onDatabaseCreate(SQLiteDatabase db) {
        String CREATE_EVENT_TABLE = String.format("CREATE TABLE %S (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT,%s TEXT, %s TEXT, %s TEXT,%s TEXT ,%s BLOB,%s BLOB ,%s BLOB,%s TEXT)",
                TABLE_EVENTS, EVENT_ID, LICENSE_CODE, INTERFACE_ID, LUID, SUID, CUID, CATEGORY, EVENT_NAME, EVENT_TIME, EVENT_DATA, SYSTEM_DATA, SYNC_STATE);
        db.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onDatabaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " LIMIT 0", null);
            if (cursor.getColumnIndex(INTERFACE_ID) == -1) {
                db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_EVENTS));
                this.onDatabaseCreate(db);
            }
        } catch (SQLException e) {
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_EVENTS));
            this.onDatabaseCreate(db);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onDatabaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_EVENTS));
        this.onDatabaseCreate(db);
    }

    @Override
    public int getOldestData() {
        String query = "SELECT " + EVENT_ID + " FROM " + TABLE_EVENTS + " ORDER BY " + EVENT_TIME + " ASC LIMIT 0,1";
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


    public void saveEventData(ArrayList<EventPayload> eventPayloadArrayList) {
        if (eventPayloadArrayList != null) {
            for (EventPayload eventPayload : eventPayloadArrayList) {
                saveEventData(eventPayload);
            }
        }
    }


    public long saveEventData(EventPayload eventPayload) {
        if (getDatabaseSize() > MAX_DB_SIZE) {
            int oldestDataId = getOldestData();
            if (oldestDataId != -1) {
                deleteRow(TABLE_EVENTS, EVENT_ID + " = ?", new String[]{Integer.toString(oldestDataId)});
            }
        }
        ContentValues values = new ContentValues();
        values.put(LICENSE_CODE, eventPayload.getLicenseCode());
        values.put(INTERFACE_ID, eventPayload.getInterfaceId());
        values.put(LUID, eventPayload.getLUID());
        values.put(SUID, eventPayload.getSUID());
        values.put(CUID, eventPayload.getCUID());
        values.put(CATEGORY, eventPayload.getCategory());
        values.put(EVENT_NAME, eventPayload.getEventName());
        values.put(EVENT_TIME, WebEngageUtils.serialize(eventPayload.getEventTime()));
        Object eventData = eventPayload.getEventData();
        values.put(EVENT_DATA, WebEngageUtils.serialize(eventData));


        if (eventPayload.getSystemData() == null) {
            values.put(SYSTEM_DATA, WebEngageUtils.serialize(new HashMap<String, Object>()));
        } else {
            values.put(SYSTEM_DATA, WebEngageUtils.serialize(eventPayload.getSystemData()));
        }
        values.put(SYNC_STATE, "NOT_SYNCED");
        long rowId = insert(TABLE_EVENTS, values);


        return rowId;


    }

    public synchronized ArrayList<EventPayload> getEventData(int numberOfRows) {
        ArrayList<EventPayload> eds = new ArrayList<EventPayload>();
        String query = "select  * from " + TABLE_EVENTS + " where sync_state=\"NOT_SYNCED\" or sync_state=\"FAILED\" ORDER BY " + EVENT_TIME + " ASC LIMIT 0," + numberOfRows;
        Cursor cursor = readableRawQuery(query, null);
        EventPayload ed = null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYNC_STATE, "SYNCING");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                List<String> ids = new ArrayList<String>();
                do {

                    ed = new EventPayload();
                    ed.setId(cursor.getInt(cursor.getColumnIndex(EVENT_ID)));
                    ed.setInterfaceId(cursor.getString(cursor.getColumnIndex(INTERFACE_ID)));
                    ed.setLicenseCode(cursor.getString(cursor.getColumnIndex(LICENSE_CODE)));
                    ed.setLUID(cursor.getString(cursor.getColumnIndex(LUID)));
                    ed.setSUID(cursor.getString(cursor.getColumnIndex(SUID)));
                    ed.setCUID(cursor.getString(cursor.getColumnIndex(CUID)));
                    ed.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
                    ed.setEventName(cursor.getString(cursor.getColumnIndex(EVENT_NAME)));
                    ed.setEventTime((Date) WebEngageUtils.deserialize(cursor.getBlob(cursor.getColumnIndex(EVENT_TIME))));
                    ed.setEventData((Map<String, Object>) WebEngageUtils.deserialize(cursor.getBlob(cursor.getColumnIndex(EVENT_DATA))));
                    ed.setSystemData((Map<String, Object>) WebEngageUtils.deserialize(cursor.getBlob(cursor.getColumnIndex(SYSTEM_DATA))));

                    ids.add(Integer.toString(ed.getId()));
                    eds.add(ed);
                } while (cursor.moveToNext());
                cursor.close();
                if (ids.size() > 0) {
                    update(TABLE_EVENTS, contentValues, EVENT_ID + " in (" + new String(new char[ids.size() - 1]).replaceAll("\0", "?,") + "?)", ids.toArray(new String[ids.size()]));
                }
                return eds;
            }
            cursor.close();

        }
        return eds;

    }

    @Override
    public void deleteRow(int id) {
        deleteRow(TABLE_EVENTS, EVENT_ID + " = ?", new String[]{Integer.toString(id)});
    }

    public List<EventPayload> getAllEventsData() {
        String query = "select  * from " + TABLE_EVENTS + " where sync_state=\"NOT_SYNCED\" or sync_state=\"FAILED\" ORDER BY " + EVENT_TIME + " ASC";
        List<EventPayload> eds = new ArrayList<EventPayload>();
        Cursor cursor = readableRawQuery(query, null);
        byte[] data = null;
        EventPayload ed = null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYNC_STATE, "SYNCING");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                List<String> ids = new ArrayList<String>();
                do {

                    ed = new EventPayload();
                    ed.setId(cursor.getInt(cursor.getColumnIndex(EVENT_ID)));
                    ed.setInterfaceId(cursor.getString(cursor.getColumnIndex(INTERFACE_ID)));
                    ed.setLicenseCode(cursor.getString(cursor.getColumnIndex(LICENSE_CODE)));
                    ed.setLUID(cursor.getString(cursor.getColumnIndex(LUID)));
                    ed.setSUID(cursor.getString(cursor.getColumnIndex(SUID)));
                    ed.setCUID(cursor.getString(cursor.getColumnIndex(CUID)));
                    ed.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
                    ed.setEventName(cursor.getString(cursor.getColumnIndex(EVENT_NAME)));
                    ed.setEventTime((Date) WebEngageUtils.deserialize(cursor.getBlob(cursor.getColumnIndex(EVENT_TIME))));
                    ed.setEventData((Map<String, Object>) WebEngageUtils.deserialize(cursor.getBlob(cursor.getColumnIndex(EVENT_DATA))));
                    ed.setSystemData((Map<String, Object>) WebEngageUtils.deserialize(cursor.getBlob(cursor.getColumnIndex(SYSTEM_DATA))));

                    eds.add(ed);
                    ids.add(Integer.toString(ed.getId()));
                } while (cursor.moveToNext());
                cursor.close();
                if (ids.size() > 0) {
                    update(TABLE_EVENTS, contentValues, EVENT_ID + " in (" + new String(new char[ids.size() - 1]).replaceAll("\0", "?,") + "?)", ids.toArray(new String[ids.size()]));
                }
                return eds;
            }
            cursor.close();
        }
        return eds;

    }

    public int removeEvents(List<String> idsToRemove) {
        if (idsToRemove == null) {
            return 0;
        }
        if (idsToRemove.size() > 0) {
            return deleteRow(TABLE_EVENTS, EVENT_ID + " in (" + new String(new char[idsToRemove.size() - 1]).replaceAll("\0", "?,") + "?)", idsToRemove.toArray(new String[idsToRemove.size()]));
        }
        return 0;
    }

    public int updateFailedEvents(List<String> failedIds) {
        if (failedIds == null) {
            return 0;
        }
        if (failedIds.size() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SYNC_STATE, "FAILED");
            return update(TABLE_EVENTS, contentValues, EVENT_ID + " in (" + new String(new char[failedIds.size() - 1]).replaceAll("\0", "?,") + "?)", failedIds.toArray(new String[failedIds.size()]));
        }
        return 0;

    }

    /**
     * Changes {@link #SYNC_STATE} of events with state 'SYNCING' to 'NOT_SYNCED'.
     * This method is used to avail the events which were marked as 'SYNCING' but never deleted or marked as 'FAILED' due to unexpected termination of Service.
     * @return number of rows affected
     */
    public int updateSyncingEvents() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYNC_STATE, "NOT_SYNCED");
        return update(TABLE_EVENTS, contentValues, "sync_state=\"SYNCING\"", null);
    }

    public int updateEventData(EventPayload ed) {
        ContentValues values = new ContentValues();
        values.put(EVENT_NAME, ed.getEventName());
        values.put(EVENT_DATA, ed.getEventData().toString());
        values.put(EVENT_TIME, WebEngageUtils.serialize(ed.getEventTime()));
        return update(TABLE_EVENTS, values, EVENT_ID + " = ?", new String[]{String.valueOf(ed.getId())});

    }

    public int getEventCount() {
        String query = "select count(*) from " + TABLE_EVENTS + " where sync_state=\"NOT_SYNCED\" or sync_state=\"FAILED\"";
        Cursor cursor = readableRawQuery(query, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public static boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
