package nego.reminders.Functions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;

import nego.reminders.Costants;
import nego.reminders.Receiver.AlarmReceiver;
import nego.reminders.Receiver.BootReceiver;
import nego.reminders.Reminder;
import nego.reminders.TimelineWidget;
import nego.reminders.Upcoming;
import nego.reminders.Utils;
import nego.reminders.database.DbAdapter;

public class AlarmF {

    public static void widgetUpdate(Context context) {

        Intent i = new Intent(context, Upcoming.class);
        i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(i);

        Intent tw = new Intent(context, TimelineWidget.class);
        tw.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(tw);


    }

    public static void addAlarm(Context context, int id, long time, String repeat) {

        AlarmManager alarmMgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, AlarmReceiver.class);
        intent.setAction(Costants.ALARM_ACTION);
        intent.putExtra(Costants.EXTRA_REMINDER_ID, "" + id);
        PendingIntent alarmIntent= PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        switch (repeat) {
            case Costants.ALARM_REPEAT_DAY:
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, alarmIntent);
                break;
            case Costants.ALARM_REPEAT_WEEK:
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY * 7, alarmIntent);
                break;
            case Costants.ALARM_REPEAT_MONTH:
                Calendar c = Calendar.getInstance();
                long today = c.getTimeInMillis();

                while(time < today) {
                    c.setTimeInMillis(time);
                    if (c.get(Calendar.MONTH) == 11) {
                        c.set(Calendar.MONTH, 0);
                        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
                        time = c.getTimeInMillis();
                    } else {
                        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
                        time = c.getTimeInMillis();
                    }
                }

                if (Build.VERSION.SDK_INT >= 19)
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
                else if (Build.VERSION.SDK_INT >= 15)
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, alarmIntent);
                break;
            case Costants.ALARM_REPEAT_YEAR:
                Calendar c1 = Calendar.getInstance();
                long today1 = c1.getTimeInMillis();

                while (time < today1) {
                    c1.setTimeInMillis(time);
                    c1.set(Calendar.YEAR, c1.get(Calendar.YEAR) + 1);
                    time = c1.getTimeInMillis();
                }

                if (Build.VERSION.SDK_INT >= 19)
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
                else if (Build.VERSION.SDK_INT >= 15)
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, alarmIntent);
                break;
            default:
                if (Build.VERSION.SDK_INT >= 19)
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
                else if (Build.VERSION.SDK_INT >= 15)
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, alarmIntent);
        }
    }

    public static void deleteAlarm(Context context, int id) {
        AlarmManager manager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.setAction(Costants.ALARM_ACTION);
        intent.putExtra(Costants.EXTRA_REMINDER_ID, "" + id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(alarmIntent);
        alarmIntent.cancel();
    }

    public static void updateAlarm(Context context, int id, long time, String repeat) {
        deleteAlarm(context, id);
        addAlarm(context, id, time, repeat);
    }
}
