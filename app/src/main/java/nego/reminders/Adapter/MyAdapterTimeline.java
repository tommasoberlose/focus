package nego.reminders.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nego.reminders.AddItem;
import nego.reminders.Costants;
import nego.reminders.Item;
import nego.reminders.Main;
import nego.reminders.Place;
import nego.reminders.R;
import nego.reminders.Reminder;
import nego.reminders.Utils;
import nego.reminders.database.DbAdapter;
import nego.reminders.database.DbAdapterP;


public class MyAdapterTimeline extends RecyclerView.Adapter<MyAdapterTimeline.ViewHolder> {
    private List<Item> mDataset = new ArrayList<Item>();
    private Context mContext;

    private TextView title;
    private TextView subtitle;
    private LinearLayout items;

    private TextView day;
    private TextView month;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout v;
        public ViewHolder(LinearLayout v) {
            super(v);
            this.v = v;
        }

        public TextView title;
        public TextView subtitle;
        public TextView day;
        public TextView month;
        public LinearLayout item;
        public ViewHolder(LinearLayout v, TextView title, TextView subtitle, TextView day, TextView month, LinearLayout item) {
            super(v);
            this.v = v;
            this.title = title;
            this.subtitle = subtitle;
            this.day = day;
            this.month = month;
            this.item = item;
        }

    }

    public MyAdapterTimeline(DbAdapter dbHelper, Context mContext) {
        generate_list(dbHelper);
        this.mContext = mContext;
    }

    @Override
    public MyAdapterTimeline.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        ViewHolder vh;
        View v;

        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_noitem_bydate, parent, false);
            vh = new ViewHolder((LinearLayout) v);
        } else if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.top_divider_timeline, parent, false);
            vh = new ViewHolder((LinearLayout) v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_bydate, parent, false);
            vh = new ViewHolder((LinearLayout) v, (TextView) v.findViewById(R.id.title), (TextView) v.findViewById(R.id.subtitle), (TextView) v.findViewById(R.id.day), (TextView) v.findViewById(R.id.month), (LinearLayout) v.findViewById(R.id.items));
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (mDataset.get(position).getType() == 4) {
            // TITLE
            title = holder.title;
            title.setText(mDataset.get(position).getItem().getTitle());

            // INFO
            subtitle = holder.subtitle;
            if (mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_NOTIME)) {
                subtitle.setText(mContext.getResources().getString(R.string.created) + ": "+getDate(mDataset.get(position).getItem().getDate_create()));
            }
            if (mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_TIME)) {
                if (mDataset.get(position).getItem().getDate_reminded() == 0)
                    subtitle.setText(mContext.getResources().getString(R.string.snoozed) + ": "+getDate(Long.parseLong(mDataset.get(position).getItem().getAlarm_info())));
                else if (mDataset.get(position).getItem().getDate_reminded() != 0)
                    subtitle.setText(mContext.getResources().getString(R.string.reminded) + ": "+getDate(mDataset.get(position).getItem().getDate_reminded()));
            }
            if (mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_WHERE)) {
                if (mDataset.get(position).getItem().getDate_reminded() == 0)
                    subtitle.setText(mContext.getResources().getString(R.string.snoozed) + ": "+getPlace(mDataset.get(position).getItem().getAlarm_info()).getName());
                else if (mDataset.get(position).getItem().getDate_reminded() != 0)
                    subtitle.setText(mContext.getResources().getString(R.string.reminded) + ": " + getPlace(mDataset.get(position).getItem().getAlarm_info()).getName());
            }
            if (mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_BLUETOOTH) || mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_WIFI)) {
                if (mDataset.get(position).getItem().getDate_reminded() == 0)
                    subtitle.setText(mContext.getResources().getString(R.string.snoozed) + ": " +mDataset.get(position).getItem().getAlarm_info().split("_")[1]);
                else if (mDataset.get(position).getItem().getDate_reminded() != 0)
                    subtitle.setText(mContext.getResources().getString(R.string.reminded) + ": " +mDataset.get(position).getItem().getAlarm_info().split("_")[1]);
            }

            // DAY & MOUNTH
            day = holder.day;
            day.setText(getDay(position));

            month = holder.month;
            month.setText(getMounth(position));

            // ON CLICK
            items = holder.item;
            items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AddItem.class);
                    intent.setAction(Costants.EXTRA_ACTION_EDIT);
                    intent.putExtra(Costants.EXTRA_REMINDER, mDataset.get(position).getItem());
                    try {
                        ((Main)mContext).startActivityForResult(intent, 1);
                    } catch (Exception e) {
                        Utils.SnackbarC(mContext, mContext.getResources().getString(R.string.error), v);
                    }
                }
            });

            if(position == mDataset.size() - 1)
                holder.v.setPadding((int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin), (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin)/2, (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin), (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin));
            else
                holder.v.setPadding((int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin), (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin)/2, (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin), 0);

            // BODY
            switch (mDataset.get(position).getItem().getType()) {
                case "PLACE":
                    items.setBackgroundResource(R.drawable.item_back_r);
                    break;
                case "HOME":
                    items.setBackgroundResource(R.drawable.item_back_r);
                    break;
                case "WORK":
                    items.setBackgroundResource(R.drawable.item_back_r);
                    break;
                case "STAR":
                    items.setBackgroundResource(R.drawable.item_back_l);
                    break;
                case "LIST":
                    items.setBackgroundResource(R.drawable.item_back_do);
                    break;
                case "TODO":
                    items.setBackgroundResource(R.drawable.item_back_p);
                    break;
                case "FIXED":
                    items.setBackgroundResource(R.drawable.item_back_p);
                    break;
                case "DEVICE":
                    items.setBackgroundResource(R.drawable.item_back_t);
                    break;
                case "WIFI":
                    items.setBackgroundResource(R.drawable.item_back_t);
                    break;
                default:
                    items.setBackgroundResource(R.drawable.item_back_b);
                    break;
            }

            if (mDataset.get(position).getItem().Checked()) {
                title.setTextColor(mContext.getResources().getColor(R.color.all_text));
                title.setLinkTextColor(mContext.getResources().getColor(R.color.all_text));
                subtitle.setTextColor(mContext.getResources().getColor(R.color.all_text));
                items.setBackgroundResource(R.drawable.item_back_c);
            } else {
                title.setTextColor(mContext.getResources().getColor(android.R.color.white));
                title.setLinkTextColor(mContext.getResources().getColor(android.R.color.white));
                subtitle.setTextColor(mContext.getResources().getColor(android.R.color.white));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Item obj = mDataset.get(position);
        return obj.getType();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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
            return HM.format(new Date(byR.getTimeInMillis()));
        } else if (today.get(Calendar.YEAR) == byR.get(Calendar.YEAR)) {
            return DM.format(new Date(byR.getTimeInMillis()));
        } else {
            return MY.format(new Date(byR.getTimeInMillis()));
        }
    }

    public String getDay(int pos) {
        Calendar d = Calendar.getInstance();
        Calendar dB = Calendar.getInstance();
        d.setTimeInMillis(mDataset.get(pos).getItem().getDate_create());
        SimpleDateFormat D = new SimpleDateFormat("d");
        if (pos <= 1) {
            return D.format(new Date(d.getTimeInMillis()));
        } else {
            dB.setTimeInMillis(mDataset.get(pos - 1).getItem().getDate_create());
            if (d.get(Calendar.YEAR) == dB.get(Calendar.YEAR) &&
                    d.get(Calendar.MONTH) == dB.get(Calendar.MONTH) &&
                    d.get(Calendar.DAY_OF_MONTH) == dB.get(Calendar.DAY_OF_MONTH)) {
                return "";
            } else {
                return D.format(new Date(d.getTimeInMillis()));
            }
        }
    }

    public String getMounth(int pos) {
        Calendar d = Calendar.getInstance();
        Calendar dB = Calendar.getInstance();
        d.setTimeInMillis(mDataset.get(pos).getItem().getDate_create());
        SimpleDateFormat M = new SimpleDateFormat("MMM");
        if (pos <= 1) {
            return M.format(new Date(d.getTimeInMillis()));
        } else {
            dB.setTimeInMillis(mDataset.get(pos - 1).getItem().getDate_create());
            if (d.get(Calendar.YEAR) == dB.get(Calendar.YEAR) &&
                    d.get(Calendar.MONTH) == dB.get(Calendar.MONTH) &&
                    d.get(Calendar.DAY_OF_MONTH) == dB.get(Calendar.DAY_OF_MONTH)) {
                return "";
            } else {
                return M.format(new Date(d.getTimeInMillis()));
            }
        }
    }



    // GENERATE LIST
    public void generate_list(DbAdapter dbHelper) {
        mDataset.clear();
        Cursor cursor;
        int count = dbHelper.fetchAllReminders().getCount();
        if (count == 0) {
            mDataset.add(new Item(1));
            mDataset.add(new Item(0));
        } else {
            mDataset.add(new Item(1));

            cursor = dbHelper.fetchAllReminders();
            while (cursor.moveToNext()) {
                Reminder pas = new Reminder(cursor);
                mDataset.add(new Item(4, pas));
            }
            cursor.close();
        }
    }


    public Place getPlace(String id) {
        DbAdapterP dbHelperP = new DbAdapterP(mContext);
        dbHelperP.open();
        Place p = null;
        Cursor c = dbHelperP.getPlaceById(id);
        if (c.moveToNext())
            p = new Place(c);
        dbHelperP.close();
        return p;
    }

    public void update(String action, Reminder r) {
        int k;
        if (mDataset.get(1).getType() == 0) {
            mDataset.remove(1);
            notifyItemRemoved(1);
        }
        switch (action) {
            case Costants.ACTION_CREATE:
                mDataset.add(1, new Item(4, r));
                notifyItemInserted(1);
                notifyItemChanged(2);
                break;
            case Costants.ACTION_UPDATE:
                for (k=1;k<mDataset.size();k++) {
                    if (mDataset.get(k).getItem().getId() == r.getId()) {
                        mDataset.set(k, new Item(4, r));
                        notifyItemChanged(k);
                        break;
                    }
                }
                break;
            case Costants.ACTION_DELETE:
                for (k=1;k<mDataset.size();k++) {
                    if (mDataset.get(k).getItem().getId() == r.getId()) {
                        mDataset.remove(k);
                        notifyItemRemoved(k);
                        if (k <= mDataset.size())
                            notifyItemChanged(k);
                        break;
                    }
                }
                break;
            case Costants.ACTION_UNDELETE:
                for (k=1;k<mDataset.size();k++) {
                    if (mDataset.get(k).getItem().getDate_create() < r.getDate_create()) {
                        mDataset.add(k, new Item(4, r));
                        notifyItemInserted(k);
                        if (k + 1 <= mDataset.size())
                            notifyItemChanged(k + 1);
                        break;
                    }
                }
                if (k == mDataset.size()) {
                    mDataset.add(k, new Item(4, r));
                    notifyItemInserted(k + 1);
                    if (k + 2 <= mDataset.size())
                        notifyItemChanged(k + 2);
                }
                break;
            case Costants.ACTION_REMINDED:
                if (!mDataset.contains(new Item(4,r))) {
                    for (k=1;k<mDataset.size();k++) {
                        if (mDataset.get(k).getItem().getId() == r.getId()) {
                            mDataset.set(k, new Item(4, r));
                            notifyItemChanged(k);
                            break;
                        }
                    }
                }
                break;
        }
        if (mDataset.size() == 1) {
            mDataset.add(new Item(0));
            notifyItemInserted(1);
        }
    }
}