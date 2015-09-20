package nego.reminders.Adapter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nego.reminders.AddItem;
import nego.reminders.Item;
import nego.reminders.R;
import nego.reminders.Reminder;
import nego.reminders.database.DbAdapter;


public class AdapterTimelineWidget extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AdapterStack(this.getApplicationContext(), intent);
    }
}

class AdapterStack implements RemoteViewsService.RemoteViewsFactory {
    private Item[] mDataset;
    private Context mContext;
    private int mAppWidgetId;

    public AdapterStack(Context context, Intent i) {
        mContext = context;
        mAppWidgetId = i.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        onDataSetChanged();
    }

    public void onCreate(){
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public void onDestroy() {
        mDataset = null;
        mContext = null;
    }


    public RemoteViews getViewAt(int position) {
        RemoteViews rv;
        int viewType = mDataset[position].getType();
        if (viewType == 0) {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.list_noitem_bydate);
        } else if (viewType == 1) {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.list_topdivider);
        } else if (viewType == 5) {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_bydate);
        } else {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_bydate);
        }
/*
        if (viewType == 4 || viewType == 5) {
            // TODO: eliminare le due tipologie
            // TITLE
            rv.setTextViewText(R.id.title, mDataset[position].getItem().getTitle());

            // INFO
            String[] split = mDataset[position].getItem().getSubtitle().split("_");
            if (split[0].equals("NOTIME")) {
                rv.setTextViewText(R.id.subtitle, "");
            }
            if (split[0].equals("TIME")) {
                if (mDataset[position].getItem().getDate_reminded() == 0)
                    rv.setTextViewText(R.id.subtitle, "Snooze until " + getDate(Long.parseLong(split[1])));
                else if (mDataset[position].getItem().getDate_reminded() != 0)
                    rv.setTextViewText(R.id.subtitle, "Reminded on " + getDate(Long.parseLong(split[1])));
            }
            if (split[0].equals("WHERE")) {
                if (mDataset[position].getItem().getDate_reminded() == 0)
                    rv.setTextViewText(R.id.subtitle, "Snooze at " + split[1]);
                else if (mDataset[position].getItem().getDate_reminded() != 0)
                    rv.setTextViewText(R.id.subtitle, "Reminded at Home");
            }

            // DAY & MOUNTH
            rv.setTextViewText(R.id.day, getDay(position));

            rv.setTextViewText(R.id.month, getMounth(position));

            // ON CLICK

            Intent viewIntent = new Intent(mContext, AddItem.class);
            viewIntent.putExtra("reminder_id", "" + mDataset[position].getItem().getId());
            viewIntent.setAction("EDIT");
            PendingIntent viewPendingIntent = PendingIntent.getActivity(mContext, 0, viewIntent, 0);
            rv.setOnClickPendingIntent(R.id.item, viewPendingIntent);

        }
*/
        return rv;
    }

    public int getCount() {
        return mDataset.length;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public String getDate(long date) {
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

    public String getDay(int pos) {
        Calendar d = Calendar.getInstance();
        Calendar dB = Calendar.getInstance();
        d.setTimeInMillis(mDataset[pos].getItem().getDate_create());
        SimpleDateFormat D = new SimpleDateFormat("d");
        if (pos <= 1) {
            return D.format(new Date(d.getTimeInMillis())).toString();
        } else {
            dB.setTimeInMillis(mDataset[pos - 1].getItem().getDate_create());
            if (d.get(Calendar.YEAR) == dB.get(Calendar.YEAR) &&
                    d.get(Calendar.MONTH) == dB.get(Calendar.MONTH) &&
                    d.get(Calendar.DAY_OF_MONTH) == dB.get(Calendar.DAY_OF_MONTH)) {
                return "";
            } else {
                return D.format(new Date(d.getTimeInMillis())).toString();
            }
        }
    }

    public String getMounth(int pos) {
        Calendar d = Calendar.getInstance();
        Calendar dB = Calendar.getInstance();
        d.setTimeInMillis(mDataset[pos].getItem().getDate_create());
        SimpleDateFormat M = new SimpleDateFormat("MMM");
        if (pos <= 1) {
            return M.format(new Date(d.getTimeInMillis())).toString();
        } else {
            dB.setTimeInMillis(mDataset[pos - 1].getItem().getDate_create());
            if (d.get(Calendar.YEAR) == dB.get(Calendar.YEAR) &&
                    d.get(Calendar.MONTH) == dB.get(Calendar.MONTH) &&
                    d.get(Calendar.DAY_OF_MONTH) == dB.get(Calendar.DAY_OF_MONTH)) {
                return "";
            } else {
                return M.format(new Date(d.getTimeInMillis())).toString();
            }
        }
    }



    // GENERATE LIST
    public void onDataSetChanged() {
        DbAdapter dbHelper = new DbAdapter(mContext);
        dbHelper.open();
        Cursor cursor;
        int count = dbHelper.fetchAllReminders().getCount();
        if (count == 0) {
            mDataset = new Item[]{new Item(1), new Item(0)};
        } else {
            int f = 0;
            mDataset = new Item[count];

            cursor = dbHelper.fetchAllReminders();
            while (cursor.moveToNext()) {
                Reminder pas = new Reminder(cursor);
                mDataset[f] = new Item(4, pas);
                f++;
            }
            cursor.close();
        }
        dbHelper.close();
    }

}