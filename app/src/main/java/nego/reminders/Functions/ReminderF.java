package nego.reminders.Functions;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;

import nego.reminders.Costants;
import nego.reminders.R;
import nego.reminders.Reminder;
import nego.reminders.Utils;
import nego.reminders.database.DbAdapter;

public class ReminderF {

    private static DbAdapter dbHelper;

    public static Reminder getReminder(Context context, String id) {
        dbHelper = new DbAdapter(context);
        dbHelper.open();
        Reminder r = null;
        Cursor c = dbHelper.getReminderById(id);
        while(c.moveToNext())
            r = new Reminder(c);

        dbHelper.close();
        c.close();
        return r;

    }

    // UNDO DELETE REMINDERS
    public static boolean undo_delete_reminder(Context context, Reminder r) {
        dbHelper = new DbAdapter(context);
        dbHelper.open();
        boolean done = false;

        if(r.undo_delete_reminder(context, dbHelper))
            done = true;

        dbHelper.close();
        return done;
    }

    // DELETE REMINDER
    public static boolean delete_reminder(Context context, Reminder r) {
        dbHelper = new DbAdapter(context);
        dbHelper.open();

        if(r.delete_reminder(context, dbHelper)) {
            NotificationF.CancelNotification(context, "" + r.getId());
            dbHelper.close();
            return true;
        } else {
            dbHelper.close();
            return false;
        }
    }

    // CHECK REMINDER
    public static boolean check_reminder(Context context, Reminder r) {
        boolean done = false;
        dbHelper = new DbAdapter(context);
        dbHelper.open();
        Calendar now = Calendar.getInstance();
        if (r.getType().equals("FIXED"))
            r.setType("TODO");
        if (r.getDate_reminded() == 0)
            r.setDate_reminded(now.getTimeInMillis());
        r.setDate_checked(now.getTimeInMillis());
        if(r.update_reminder(context, dbHelper)) {
            done = true;
        }
        dbHelper.close();
        return done;
    }

    // UNDO CHECK REMINDER
    public static boolean uncheck_reminder(Context context, Reminder r) {
        dbHelper = new DbAdapter(context);
        dbHelper.open();
        boolean done = false;
        r.setDate_checked(0);
        if(r.update_reminder(context, dbHelper)) {
            done = true;
        }
        dbHelper.close();
        return done;
    }

    public static String getInfoReminder(Context context, Reminder r) {

        if (r.getAlarm().equals(Costants.ALARM_TYPE_NOTIME)) {
            return context.getResources().getString(R.string.created) + ": "+Utils.getDate(r.getDate_create());
        }
        if (r.getAlarm().equals(Costants.ALARM_TYPE_TIME)) {
                return context.getResources().getString(R.string.snoozed) + ": "+Utils.getDate(Long.parseLong(r.getAlarm_info()));
        }
        if (r.getAlarm().equals(Costants.ALARM_TYPE_WHERE)) {
                return context.getResources().getString(R.string.snoozed) + ": "+ LocationF.getPlace(context, r.getAlarm_info()).getName();
        }
        if (r.getAlarm().equals(Costants.ALARM_TYPE_BLUETOOTH) || r.getAlarm().equals(Costants.ALARM_TYPE_WIFI)) {
                return  context.getResources().getString(R.string.snoozed) + ": "+r.getAlarm_info().split("_")[1];
        }
        return "";
    }

}
