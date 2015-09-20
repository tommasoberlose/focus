package nego.reminders.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.provider.ContactsContract;

import nego.reminders.Place;
import nego.reminders.R;
import nego.reminders.Reminder;

public class DbAdapterP {


    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelperPlace dbHelper;

    // Database fields
    private static final String DATABASE_TABLE_PLACE = "place";

    public static final String KEY_P_ID = "id";
    public static final String KEY_P_NAME = "name";
    public static final String KEY_P_INFO = "info";
    public static final String KEY_P_LAT = "latitudine";
    public static final String KEY_P_LONG = "longitudine";

    public DbAdapterP(Context context) {
        this.context = context;
    }

    public DbAdapterP open() throws SQLException {
        dbHelper = new DatabaseHelperPlace(context);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValuesPlace(int id, String name, String info, String latitudine, String longitudine) {
        ContentValues values = new ContentValues();
        values.put(KEY_P_ID, id);
        values.put(KEY_P_NAME, name);
        values.put(KEY_P_INFO, info);
        values.put(KEY_P_LAT, latitudine);
        values.put(KEY_P_LONG, longitudine);

        return values;
    }

    private ContentValues createContentValuesPlace(String name, String info, String latitudine, String longitudine) {
        ContentValues values = new ContentValues();
        values.put(KEY_P_NAME, name);
        values.put(KEY_P_INFO, info);
        values.put(KEY_P_LAT, latitudine);
        values.put(KEY_P_LONG, longitudine);

        return values;
    }

    //create a place
    public long createPlace(Place p) {
        ContentValues initialValues = createContentValuesPlace(p.getName(), p.getInfo(), p.getLatitude(), p.getLongitude());
        return database.insertOrThrow(DATABASE_TABLE_PLACE, null, initialValues);
    }

    //restore a place
    public long restorePlace(Place p) {
        ContentValues initialValues = createContentValuesPlace(p.getId(), p.getName(), p.getInfo(), p.getLatitude(), p.getLongitude());
        return database.insertOrThrow(DATABASE_TABLE_PLACE, null, initialValues);
    }

    //update a place
    public boolean updatePlace(int id, String name, String info, String latitudine, String longitudine) {
        ContentValues updateValues = createContentValuesPlace(id, name, info, latitudine, longitudine);
        return database.update(DATABASE_TABLE_PLACE, updateValues, KEY_P_ID + "==" + id, null) > 0;
    }

    //update a place
    public boolean updatePlace(Place p) {
        ContentValues updateValues = createContentValuesPlace(p.getId(), p.getName(), p.getInfo(), p.getLatitude(), p.getLongitude());
        return database.update(DATABASE_TABLE_PLACE, updateValues, KEY_P_ID + "==" + p.getId(), null) > 0;
    }

    //delete a place
    public boolean deletePlace(String ID) {
        return database.delete(DATABASE_TABLE_PLACE, KEY_P_ID + "==" + ID, null) > 0;
    }

    //fetch all places
    public Cursor fetchAllPlaces() {
        return database.query(DATABASE_TABLE_PLACE, new String[]{KEY_P_ID, KEY_P_NAME, KEY_P_INFO, KEY_P_LAT, KEY_P_LONG}, null, null, null, null, null);
    }

    public int getPlacesN() {
        return (database.query(DATABASE_TABLE_PLACE, new String[]{KEY_P_ID}, null, null, null, null, null)).getCount();
    }

    public Cursor getPlaceById(String id) {
        Cursor mCursor = database.query(true, DATABASE_TABLE_PLACE, new String[]{
                        KEY_P_ID, KEY_P_NAME, KEY_P_INFO, KEY_P_LAT, KEY_P_LONG},
                KEY_P_ID + " == '" + id + "'", null, null, null, null, null);

        return mCursor;
    }

    public Cursor getHomePlace(Context context) {
        Cursor mCursor = database.query(true, DATABASE_TABLE_PLACE, new String[]{
                        KEY_P_ID, KEY_P_NAME, KEY_P_INFO, KEY_P_LAT, KEY_P_LONG},
                KEY_P_INFO + " like '" + context.getResources().getString(R.string.home) + "'", null, null, null, null, null);

        return mCursor;
    }
}