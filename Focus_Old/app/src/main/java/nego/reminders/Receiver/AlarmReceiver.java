package nego.reminders.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;

import nego.reminders.Costants;
import nego.reminders.Functions.AlarmF;
import nego.reminders.Functions.NotificationF;
import nego.reminders.Functions.ReminderF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.R;
import nego.reminders.Reminder;
import nego.reminders.Utils;
import nego.reminders.database.DbAdapter;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.ALARM")) {
            Reminder r = ReminderF.getReminder(context, intent.getExtras().getString("ID_ALARM"));
            ReminderService.startAction(context, Costants.ACTION_REMINDED, r);
            //AlarmF.updateAlarm(context);
            NotificationF.Notification(context, r);
        }
    }
}