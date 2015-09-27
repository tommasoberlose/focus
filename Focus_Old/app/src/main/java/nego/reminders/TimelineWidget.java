package nego.reminders;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import nego.reminders.Adapter.AdapterTimelineWidget;

public class TimelineWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timeline_widget);
        bindRemoteAdapter(context, appWidgetId, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void bindRemoteAdapter(Context context, int widgetId, RemoteViews remoteViews) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Intent i = new Intent(context, AdapterTimelineWidget.class);
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            i.setData(Uri.parse(i.toUri(Intent.URI_INTENT_SCHEME)));

            remoteViews.setRemoteAdapter(R.id.listView, i);
            remoteViews.setEmptyView(R.id.scrollList, R.layout.list_noitem_bydate);

            Intent viewIntent = new Intent(context, Main.class);
            PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.top_w, viewPendingIntent);

            Intent addI = new Intent(context, AddItem.class);
            PendingIntent pAddI = PendingIntent.getActivity(context, 0, addI, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.add, pAddI);

            Intent i2 = new Intent(context, TimelineWidget.class);
            i2.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            PendingIntent pi2 = PendingIntent.getBroadcast(context, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.refresh, pi2);
        }
    }
}

