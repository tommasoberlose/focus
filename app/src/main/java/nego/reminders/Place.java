package nego.reminders;


import android.database.Cursor;

import nego.reminders.database.DbAdapter;
import nego.reminders.database.DbAdapterP;

public class Place {

    private int id;
    private String name;
    private String info;
    private String latitude;
    private String longitude;

    public Place(String name, String info, String latitude, String longitude) {
        this.name = name;
        this.info = info;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Place(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndex(DbAdapterP.KEY_P_ID));
        this.name = cursor.getString( cursor.getColumnIndex(DbAdapterP.KEY_P_NAME) );
        this.info = cursor.getString(cursor.getColumnIndex(DbAdapterP.KEY_P_INFO));
        this.latitude = cursor.getString(cursor.getColumnIndex(DbAdapterP.KEY_P_LAT));
        this.longitude = cursor.getString(cursor.getColumnIndex(DbAdapterP.KEY_P_LONG));
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
