package com.nego.focus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nego.reminders.Functions.LocationF;
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