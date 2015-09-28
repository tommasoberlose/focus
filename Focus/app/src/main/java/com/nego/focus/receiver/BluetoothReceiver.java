package com.nego.focus.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Calendar;

import nego.reminders.Costants;
import nego.reminders.Functions.NotificationF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.Reminder;
import nego.reminders.database.DbAdapter;

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.bluetooth.device.action.ACL_CONNECTED")) {

            DbAdapter dbHelper = new DbAdapter(context);
            dbHelper.open();
            Calendar now = Calendar.getInstance();

            Cursor cursor;
            int count = dbHelper.fetchRemindersByFilterBluetooth().getCount();
            if (count != 0) {
                cursor = dbHelper.fetchRemindersByFilterBluetooth();
                while (cursor.moveToNext()) {
                    Reminder actual = new Reminder(cursor);
                    if (actual.getAlarm().equals(Costants.ALARM_TYPE_BLUETOOTH)) {
                        if (actual.getAlarm_info().split("_")[0].equals(((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)).getAddress())) {
                            ReminderService.startAction(context, Costants.ACTION_REMINDED, actual);
                            NotificationF.Notification(context, actual);
                        }
                    }
                }
                cursor.close();
            }

            dbHelper.close();
        }
    }
}