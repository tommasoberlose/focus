package com.nego.focus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import nego.reminders.Costants;
import nego.reminders.Functions.NotificationF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.R;
import nego.reminders.Reminder;

public class CheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Costants.CHECK_ACTION)) {
            Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
            ReminderService.startAction(context, Costants.ACTION_CHECKED, r);
            NotificationF.CancelNotification(context, "" + r.getId());
            Toast.makeText(context, context.getResources().getString(R.string.reminder_checked), Toast.LENGTH_SHORT).show();
        }
    }
}