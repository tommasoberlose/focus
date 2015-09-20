package nego.reminders;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nego.reminders.database.DbAdapter;


public class Upcoming extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews viewsAll = new RemoteViews(context.getPackageName(), R.layout.upcoming);
/* TODO
        DbAdapter dbHelper = new DbAdapter(context);
        dbHelper.open();
        Cursor cursor;
        int count = dbHelper.fetchAllReminders().getCount();

        if (count == 0) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.card_widget_upcoming);
            views.setTextViewText(R.id.title, context.getResources().getString(R.string.congratulations));
            views.setTextViewText(R.id.subtitle, context.getResources().getString(R.string.noitem) + ".");

            Intent i = new Intent(context, Main.class);
            PendingIntent p = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.top_w, p);
            viewsAll.removeAllViews(R.id.back_white);
            viewsAll.addView(R.id.back_white, views);
        } else {
            SharedPreferences SP = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
            int n_row = Integer.parseInt(SP.getString("widget_upcoming_row", "3"));

            if (n_row > count)
                n_row = count;

            cursor = dbHelper.fetchRemindersByFilterTimeUpcoming();
            viewsAll.removeAllViews(R.id.back_white);


            for (int k = 0; k < n_row; k++) {
                //TODO non funziona

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.card_widget_upcoming);
                cursor.moveToNext();
                Reminder pas = new Reminder(cursor);

                // TITLE
                views.setTextViewText(R.id.title, pas.getTitle());

                // INFO
                String[] split = pas.getSubtitle().split("_");
                if (split[0].equals("NOTIME")) {
                    views.setTextViewText(R.id.subtitle, split[1]);
                }
                if (split[0].equals("TIME")) {
                    if (pas.getDate_reminded() == 0)
                        views.setTextViewText(R.id.subtitle, context.getResources().getString(R.string.snooze_until) + " "+getDate(Long.parseLong(split[1])));
                    else if (pas.getDate_reminded() != 0)
                        views.setTextViewText(R.id.subtitle, context.getResources().getString(R.string.reminded) + " "+getDate(pas.getDate_reminded()));
                }
                if (split[0].equals("WHERE")) {
                    if (pas.getDate_reminded() == 0)
                        views.setTextViewText(R.id.subtitle, context.getResources().getString(R.string.snooze_at) + " "+split[1]);
                    else if (pas.getDate_reminded() != 0)
                        views.setTextViewText(R.id.subtitle, context.getResources().getString(R.string.reminded) + " "+split[1]);
                }

                switch (pas.getType()) {
                    case "PLACE":
                        views.setTextColor(R.id.title, context.getResources().getColor(R.color.icon_r));
                        break;
                    case "WORK":
                        views.setTextColor(R.id.title, context.getResources().getColor(R.color.icon_t));
                        break;
                    case "LIST":
                        views.setTextColor(R.id.title, context.getResources().getColor(R.color.icon_i));
                        break;
                    case "TODO":
                        views.setTextColor(R.id.title, context.getResources().getColor(R.color.icon_i));
                        break;
                    default:
                        views.setTextColor(R.id.title, context.getResources().getColor(R.color.primary_dark));
                        break;
                }

                Intent i = new Intent(context, AddItem.class);
                i.setAction("EDIT");
                i.putExtra("reminder_id", "" + pas.getId());
                PendingIntent p = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.top_w, p);

                viewsAll.addView(R.id.back_white, views);

            }
            cursor.close();
        }
        dbHelper.close();
        appWidgetManager.updateAppWidget(appWidgetId, viewsAll);
        */
    }

    public static String getDate(long date) {
        Calendar today = Calendar.getInstance();
        Calendar byR = Calendar.getInstance();
        byR.setTimeInMillis(date);
        SimpleDateFormat HM = new SimpleDateFormat("HH:mm");
        SimpleDateFormat DM = new SimpleDateFormat("MMM d");
        SimpleDateFormat MY = new SimpleDateFormat("MMM y");
        if (today.get(Calendar.YEAR) == byR.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == byR.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == byR.get(Calendar.DAY_OF_MONTH)) {
            return HM.format(new Date(byR.getTimeInMillis())).toString();
        } else if (today.get(Calendar.YEAR) == byR.get(Calendar.YEAR)) {
            return DM.format(new Date(byR.getTimeInMillis())).toString();
        } else {
            return MY.format(new Date(byR.getTimeInMillis())).toString();
        }
    }
}

