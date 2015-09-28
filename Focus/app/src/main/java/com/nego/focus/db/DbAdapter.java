package com.nego.focus.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nego.focus.Costants;
import com.nego.focus.Reminder;

public class DbAdapter {


    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    // Database fields
    private static final String DATABASE_TABLE = "reminders";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TYPE = "type";
    public static final String KEY_ALARM = "alarm";
    public static final String KEY_ALARM_INFO = "alarm_info";
    public static final String KEY_DATE_CREATE = "date_create";
    public static final String KEY_DATE_REMINDED = "date_reminded";
    public static final String KEY_DATE_CHECKED = "date_checked";

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        if (database.getVersion() < DatabaseHelper.DATABASE_VERSION) {
            if (database.getVersion() == 2) {
                // SALVO VECCHI REMINDERS
                dbHelper.onCreate(database);
                Cursor c = this.fetchAllReminders();
                int id = c.getInt(c.getColumnIndex(DbAdapter.KEY_ID));
                dbHelper.close();

                // AGGIORNO DATABASE
                dbHelper.onUpgrade(database, database.getVersion(), DatabaseHelper.DATABASE_VERSION);

                // REINSERISCO VECCHI REMINDER
                while (c.moveToNext()) {
                    String[] sub = c.getString(c.getColumnIndex("subtitle")).split("_");
                    String alarm = "";
                    String alarm_info = "";
                    if (sub.length > 0)
                        alarm = sub[0];
                    if (sub.length > 1)
                        alarm_info = sub[1];
                    if (sub.length > 2)
                        alarm_info = alarm_info + sub[2];
                    this.updateReminder(
                            c.getInt(c.getColumnIndex(DbAdapter.KEY_ID)),
                            c.getString(c.getColumnIndex(DbAdapter.KEY_TITLE)),
                            "",
                            c.getString(c.getColumnIndex(DbAdapter.KEY_TYPE)),
                            alarm,
                            alarm_info,
                            c.getLong(c.getColumnIndex(DbAdapter.KEY_DATE_CREATE)),
                            c.getLong(c.getColumnIndex(DbAdapter.KEY_DATE_REMINDED)),
                            c.getLong(c.getColumnIndex(DbAdapter.KEY_DATE_CHECKED))
                    );
                }
                c.close();
            } else {
                dbHelper.onUpgrade(database, database.getVersion(), DatabaseHelper.DATABASE_VERSION);
            }
        } else {
            dbHelper.onCreate(database);
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValues(int ID, String title, String content, String type, String alarm, String alarm_info, long date_created, long date_reminded, long date_checked) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, ID);
        values.put(KEY_TITLE, title);
        values.put(KEY_CONTENT, content);
        values.put(KEY_TYPE, type);
        values.put(KEY_ALARM, alarm);
        values.put(KEY_ALARM_INFO, alarm_info);
        values.put(KEY_DATE_CREATE, date_created);
        values.put(KEY_DATE_REMINDED, date_reminded);
        values.put(KEY_DATE_CHECKED, date_checked);

        return values;
    }

    private ContentValues createContentValues(String title, String content, String type, String alarm, String alarm_info, long date_created, long date_reminded, long date_checked) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_CONTENT, content);
        values.put(KEY_TYPE, type);
        values.put(KEY_ALARM, alarm);
        values.put(KEY_ALARM_INFO, alarm_info);
        values.put(KEY_DATE_CREATE, date_created);
        values.put(KEY_DATE_REMINDED, date_reminded);
        values.put(KEY_DATE_CHECKED, date_checked);

        return values;
    }


    //create a reminder
    public long createReminder(String title, String content, String type, String alarm, String alarm_info, long date_created, long date_reminded) {
        ContentValues initialValues = createContentValues(title, content, type, alarm, alarm_info, date_created, date_reminded, 0);
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    //create a reminder
    public long createReminder(Reminder r) {
        ContentValues initialValues = createContentValues(r.getTitle(), r.getContent(), r.getType(), r.getAlarm(), r.getAlarm_info(), r.getDate_create(), r.getDate_reminded(), 0);
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    //restore a reminder
    public long restoreReminder(Reminder r) {
        ContentValues initialValues = createContentValues(r.getId(),r.getTitle(), r.getContent(), r.getType(), r.getAlarm(), r.getAlarm_info(), r.getDate_create(), r.getDate_reminded(), r.getDate_checked());
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    //update a reminder
    public boolean updateReminder(int ID, String title, String subtitle, String type, String alarm, String alarm_info, long date_created, long date_reminded, long date_checked) {
        ContentValues updateValues = createContentValues(ID, title, subtitle, type, alarm, alarm_info, date_created, date_reminded, date_checked);
        return database.update(DATABASE_TABLE, updateValues, KEY_ID + "==" + ID, null) > 0;
    }

    //update a reminder
    public boolean updateReminder(Reminder r) {
        ContentValues updateValues = createContentValues(r.getId(), r.getTitle(), r.getContent(), r.getType(), r.getAlarm(), r.getAlarm_info(), r.getDate_create(), r.getDate_reminded(), r.getDate_checked());
        return database.update(DATABASE_TABLE, updateValues, KEY_ID + "==" + r.getId(), null) > 0;
    }

    //delete a reminder
    public boolean deleteReminder(String ID) {
        return database.delete(DATABASE_TABLE, KEY_ID + "==" + ID, null) > 0;
    }

    //fetch all reminders
    public Cursor fetchAllReminders() {
        return database.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_TYPE, KEY_ALARM, KEY_ALARM_INFO, KEY_DATE_CREATE, KEY_DATE_REMINDED, KEY_DATE_CHECKED}, null, null, null, null, KEY_DATE_CREATE + " DESC");
    }

    //fetch reminders filter by a string
    public Cursor fetchRemindersByFilterTitle(String filter) {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_TYPE, KEY_ALARM, KEY_ALARM_INFO, KEY_DATE_CREATE, KEY_DATE_REMINDED, KEY_DATE_CHECKED},
                KEY_TITLE + " like '%" + filter + "%'", null, null, null,  KEY_DATE_CREATE + " DESC", null);

        return mCursor;
    }

    //fetch reminders filter by id
    public Cursor getReminderById(String id) {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_TYPE, KEY_ALARM, KEY_ALARM_INFO, KEY_DATE_CREATE, KEY_DATE_REMINDED, KEY_DATE_CHECKED},
                KEY_ID + " == '" + id + "'", null, null, null, KEY_DATE_CREATE + " DESC", null);

        return mCursor;
    }



    public Cursor fetchRemindersByFilterTodo() {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_TYPE, KEY_ALARM, KEY_ALARM_INFO, KEY_DATE_CREATE, KEY_DATE_REMINDED, KEY_DATE_CHECKED},
                KEY_DATE_CHECKED + " == '0' AND ( " + KEY_ALARM + " = '" + Costants.ALARM_TYPE_NOTIME + "' OR " + KEY_DATE_REMINDED + " != '0' )", null, null, null,  KEY_DATE_REMINDED + " DESC", null);

        return mCursor;
    }


    public Cursor fetchRemindersByFilterBluetooth() {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_TYPE, KEY_ALARM, KEY_ALARM_INFO, KEY_DATE_CREATE, KEY_DATE_REMINDED, KEY_DATE_CHECKED},
                KEY_ALARM + " like '" + Costants.ALARM_TYPE_BLUETOOTH + "' AND " + KEY_DATE_REMINDED + " == '0'", null, null, null,  KEY_DATE_CREATE + " DESC", null);

        return mCursor;
    }


    public Cursor fetchRemindersByFilterWifi() {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_TYPE, KEY_ALARM, KEY_ALARM_INFO, KEY_DATE_CREATE, KEY_DATE_REMINDED, KEY_DATE_CHECKED},
                KEY_ALARM + " like '" + Costants.ALARM_TYPE_WIFI + "' AND " + KEY_DATE_REMINDED + " == '0'", null, null, null,  KEY_DATE_CREATE + " DESC", null);

        return mCursor;
    }


}