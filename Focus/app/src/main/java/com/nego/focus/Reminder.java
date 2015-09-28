package com.nego.focus;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.nego.focus.db.DbAdapter;

public class Reminder implements Parcelable {
    private int id;
    private String title;
    private String content;
    private String type;
    private String alarm;
    private String alarm_info;
    private long date_create;
    private long date_reminded;
    private long date_checked;

    public Reminder(String title, String content, String type, String alarm, String alarm_info, long date_create){
        this.type = type;
        this.title = title;
        this.content = content;
        this.alarm = alarm;
        this.alarm_info = alarm_info;
        this.date_create = date_create;
    }

    public Reminder(String title, String content, String type, String alarm, String alarm_info, long date_create, long date_reminded){
        this.title = title;
        this.content = content;
        this.type = type;
        this.alarm = alarm;
        this.alarm_info = alarm_info;
        this.date_create = date_create;
        this.date_reminded = date_reminded;
    }

    public Reminder(int id, String title, String content, String type, String alarm, String alarm_info, long date_create, long date_reminded, long date_checked){
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.alarm = alarm;
        this.alarm_info = alarm_info;
        this.date_create = date_create;
        this.date_reminded = date_reminded;
        this.date_checked = date_checked;
    }

    public Reminder(Cursor cursor){
        this.id = cursor.getInt( cursor.getColumnIndex(DbAdapter.KEY_ID) );
        this.title = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TITLE));
        this.content = cursor.getString( cursor.getColumnIndex(DbAdapter.KEY_CONTENT) );
        this.type = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TYPE));
        this.alarm = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_ALARM));
        this.alarm_info = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_ALARM_INFO));
        this.date_create = cursor.getLong( cursor.getColumnIndex(DbAdapter.KEY_DATE_CREATE) );
        this.date_reminded = cursor.getLong( cursor.getColumnIndex(DbAdapter.KEY_DATE_REMINDED) );
        this.date_checked = cursor.getLong( cursor.getColumnIndex(DbAdapter.KEY_DATE_CHECKED) );
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm_info(String alarm_info) {
        this.alarm_info = alarm_info;
    }

    public String getAlarm_info() {
        return alarm_info;
    }

    public void setDate_create(long date_create) {
        this.date_create = date_create;
    }

    public long getDate_create() {
        return date_create;
    }

    public void setDate_reminded(long date_reminded) {
        this.date_reminded = date_reminded;
    }

    public long getDate_reminded() {
        return date_reminded;
    }

    public void setDate_checked(long date_checked) {
        this.date_checked = date_checked;
    }

    public long getDate_checked() {
        return date_checked;
    }

    public boolean Checked() {
        if(date_checked != 0) return true;
        else return false;
    }

    public boolean create_reminder(Context context, DbAdapter dbHelper) {
        if (dbHelper.createReminder(this) > 0) {
            Utils.update_receiver(context);
         return true;
        }
        return false;
    }

    public boolean update_reminder(Context context, DbAdapter dbHelper) {
        if (dbHelper.updateReminder(this)) {
            Utils.update_receiver(context);
            return true;
        }
        return false;
    }

    public boolean update_reminder(DbAdapter dbHelper) {
        return dbHelper.updateReminder(this);
    }

    public boolean delete_reminder(Context context, DbAdapter dbHelper) {
        if ( dbHelper.deleteReminder("" + this.getId())) {
            Utils.update_receiver(context);
            return true;
        }
        return false;
    }

    public boolean undo_delete_reminder(Context context, DbAdapter dbHelper) {
        if (dbHelper.restoreReminder(this) > 0) {
            Utils.update_receiver(context);
            return true;
        }
        return false;
    }

    public boolean isEqual(Reminder k) {
        if (this.id == k.getId() &&
                this.title.equals(k.getTitle()) &&
                this.content.equals(k.getContent()) &&
                this.type.equals(k.getType()) &&
                this.date_create == k.getDate_create() &&
                this.date_reminded == k.getDate_reminded() &&
                this.date_checked == k.getDate_checked()
                )
            return true;
        return false;
    }

    // PARCELIZZAZIONE

    public static final Parcelable.Creator<Reminder> CREATOR = new Creator<Reminder>() {
        public Reminder createFromParcel(Parcel source) {
            return new Reminder(source.readInt(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readLong(), source.readLong(), source.readLong());
        }
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(type);
        dest.writeString(alarm);
        dest.writeString(alarm_info);
        dest.writeLong(date_create);
        dest.writeLong(date_reminded);
        dest.writeLong(date_checked);
    }
}
