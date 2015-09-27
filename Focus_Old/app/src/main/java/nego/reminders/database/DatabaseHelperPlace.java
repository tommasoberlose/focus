package nego.reminders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperPlace extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminderdb";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_PLACE = "create table IF NOT EXISTS place (id integer primary key autoincrement, name text not null, info text not null, latitudine text not null, longitudine text not null);";

    public DatabaseHelperPlace(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_PLACE);
    }

    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {

        database.execSQL("DROP TABLE IF EXISTS place");
        onCreate(database);

    }
}