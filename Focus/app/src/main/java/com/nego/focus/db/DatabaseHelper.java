package com.nego.focus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminderdb";
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table IF NOT EXISTS reminders (id integer primary key autoincrement, title text not null, content text default '', type text default '', alarm text default '', alarm_info text default '', date_create long not null, date_reminded long default '0', date_checked long default '0');";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
        if (oldVersion == 2) {
            database.execSQL("DROP TABLE IF EXISTS reminders");
            onUpgrade(database, 3, DATABASE_VERSION);
        }
        onCreate(database);

    }
}