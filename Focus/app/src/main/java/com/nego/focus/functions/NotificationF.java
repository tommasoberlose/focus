package com.nego.focus.functions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import nego.reminders.Costants;
import nego.reminders.Main;
import nego.reminders.R;
import nego.reminders.Receiver.CheckReceiver;
import nego.reminders.Reminder;
import nego.reminders.Utils;

public class NotificationF {

    // NOTIFICATION
    public static void Notification(Context context, Reminder r) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent i=new Intent(context,Main.class);
        PendingIntent pi= PendingIntent.getActivity(context, r.getId(), i, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent s = new Intent(context,Main.class);
        s.putExtra(Costants.EXTRA_REMINDER, r);
        s.setAction(Costants.EXTRA_ACTION_EDIT);
        PendingIntent ps = PendingIntent.getActivity(context, r.getId(), s, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent c = new Intent(context,CheckReceiver.class);
        c.putExtra(Costants.EXTRA_REMINDER, r);
        c.setAction(Costants.CHECK_ACTION);
        PendingIntent pc = PendingIntent.getBroadcast(context, r.getId(), c, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder n  = new NotificationCompat.Builder(context)
                .setContentTitle(r.getTitle())
                .setContentText(ReminderF.getInfoReminder(context, r))
                .setSmallIcon(R.drawable.ic_stat_bookmark_check)
                .setContentIntent(pi)
                .setColor(context.getResources().getColor(R.color.primary))
                .addAction(R.drawable.ic_action_recently_small, context.getResources().getString(R.string.snooze), ps)
                .addAction(R.drawable.ic_action_done_small, context.getResources().getString(R.string.action_check), pc)
                .setAutoCancel(true);

        if (!r.getType().equals(Costants.ALARM_TYPE_FIXED)) {
            n.setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[]{0, 400, 400, 400})
                    .setLights(context.getResources().getColor(R.color.primary_dark), 3000, 3000)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        } else {
            n.setOngoing(true)
                    .setPriority(-1)
                    .setPriority(Notification.PRIORITY_MIN);
        }

        notificationManager.notify(r.getId(), n.build());
    }

    // NOTIFICATION ADD
    public static void NotificationAdd(Context context) {
        Intent i=new Intent(context,Main.class);
        i.setAction(Costants.EXTRA_ACTION_ADD);
        PendingIntent pi= PendingIntent.getActivity(context, -1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder n  = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.title_activity_add_item))
                .setContentText(Utils.itemsToDo(context) + " " + context.getResources().getString(R.string.num_items_todo))
                .setSmallIcon(R.drawable.ic_stat_bookmark_plus)
                .setContentIntent(pi)
                .setOngoing(true)
                .setPriority(-1)
                .setColor(context.getResources().getColor(R.color.primary))
                .setPriority(Notification.PRIORITY_MIN)
                .setAutoCancel(false);

        notificationManager.notify(-1, n.build());
    }

    public static void CancelAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void CancelNotification(Context context, String id) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(id));
    }

}
