package nego.reminders.Receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.Calendar;

import nego.reminders.Adapter.MyAdapter;
import nego.reminders.Costants;
import nego.reminders.Functions.AlarmF;
import nego.reminders.Functions.NotificationF;
import nego.reminders.Functions.ReminderF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.Reminder;
import nego.reminders.Utils;
import nego.reminders.database.DbAdapter;


public class WiFiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.net.wifi.STATE_CHANGE") && ((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).isConnected()) {

            DbAdapter dbHelper = new DbAdapter(context);
            dbHelper.open();
            Calendar now = Calendar.getInstance();
            WifiManager wifiM = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            Cursor cursor;
            int count = dbHelper.fetchAllReminders().getCount();
            if (count != 0) {
                cursor = dbHelper.fetchRemindersByFilterWifi();
                while (cursor.moveToNext()) {
                    Reminder actual = new Reminder(cursor);
                    if (actual.getAlarm().equals(Costants.ALARM_TYPE_WIFI)) {
                        if (actual.getAlarm_info().split("_")[0].equals("" + wifiM.getConnectionInfo().getNetworkId())) {
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