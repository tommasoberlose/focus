package com.nego.focus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nego.reminders.Costants;
import nego.reminders.Functions.NotificationF;
import nego.reminders.Functions.ReminderF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.Reminder;

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