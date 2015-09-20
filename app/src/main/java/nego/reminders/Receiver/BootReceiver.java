package nego.reminders.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import nego.reminders.Functions.LocationF;
import nego.reminders.Functions.NotificationF;
import nego.reminders.LocationL;
import nego.reminders.R;
import nego.reminders.Utils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Utils.oldReminder(context);
            LocationF.LocationUpdate(context);
            Utils.notification_add_update(context);
        }
    }
}