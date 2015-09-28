package com.nego.focus.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nego.focus.AddItem;
import com.nego.focus.Costants;
import com.nego.focus.Item;
import com.nego.focus.Main;
import com.nego.focus.Reminder;
import com.nego.focus.Utils;
import com.nego.focus.db.DbAdapter;
import com.nego.focus.functions.ReminderService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Item> mDataset = new ArrayList<>();
    private Context mContext;

    private TextView title;
    private TextView subtitle;
    private ImageView icon;
    private RelativeLayout back_icon;
    private RelativeLayout item = null;
    private RelativeLayout header_item;
    private TextView header_text;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }

        public RelativeLayout message_back;
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View v, RelativeLayout message_back, TextView mTextView, ImageView mImageView) {
            super(v);
            mView = v;
            this.message_back = message_back;
            this.mTextView = mTextView;
            this.mImageView = mImageView;
        }

        public LinearLayout suggestion_back;
        public TextView suggestion_title;
        public TextView suggestion_add;
        public ViewHolder(View v, LinearLayout suggestion_back, TextView suggestion_title, TextView suggestion_add) {
            super(v);
            mView = v;
            this.suggestion_back = suggestion_back;
            this.suggestion_title = suggestion_title;
            this.suggestion_add = suggestion_add;
        }

        public RelativeLayout back_item;
        public RelativeLayout header_item;
        public TextView header_text;
        public RelativeLayout check_back;
        public TextView title;
        public TextView subtitle;
        public ImageView icon;
        public RelativeLayout back_icon;
        public ImageView controller;
        public ViewHolder(View v, RelativeLayout back_item, RelativeLayout check_back, RelativeLayout header_item, TextView header_text, TextView title, TextView subtitle, ImageView icon, RelativeLayout back_icon, ImageView controller) {
            super(v);
            mView = v;
            this.back_item = back_item;
            this.header_item = header_item;
            this.header_text = header_text;
            this.check_back = check_back;
            this.title = title;
            this.subtitle = subtitle;
            this.icon = icon;
            this.back_icon = back_icon;
            this.controller = controller;
        }

    }

    public MyAdapter(DbAdapter dbHelper, String query, Context mContext) {
        this.mContext = mContext;
        generate_list(dbHelper, query);
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        ViewHolder vh;
        View v;

        if(viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_noitem, parent, false);
            vh = new ViewHolder(v);
        } else if(viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_topdivider, parent, false);
            vh = new ViewHolder(v);
        } else if(viewType == 2) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_topdivider_small, parent, false);
            vh = new ViewHolder(v);
        } else if(viewType == 5) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_top_card, parent, false);
            vh = new ViewHolder(v);
        } else if(viewType == 6) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_bottom_card, parent, false);
            vh = new ViewHolder(v);
        } else if(viewType == 7) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_suggestion, parent, false);
            vh = new ViewHolder(v, (LinearLayout) v.findViewById(R.id.back_item), (TextView) v.findViewById(R.id.title), (TextView) v.findViewById(R.id.action_add));
        } else if(viewType == 8) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_message, parent, false);
            vh = new ViewHolder(v, (RelativeLayout) v.findViewById(R.id.message), (TextView) v.findViewById(R.id.textView), (ImageView) v.findViewById(R.id.close_m));
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_1, parent, false);
            vh = new ViewHolder(v, (RelativeLayout) v.findViewById(R.id.back_item), (RelativeLayout) v.findViewById(R.id.check_back), (RelativeLayout) v.findViewById(R.id.header_item), (TextView) v.findViewById(R.id.header_text), (TextView) v.findViewById(R.id.title), (TextView) v.findViewById(R.id.subtitle), (ImageView) v.findViewById(R.id.icon), (RelativeLayout) v.findViewById(R.id.back_icon), (ImageView) v.findViewById(R.id.controller));
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mDataset.get(position).getType() == 8) {
            holder.message_back.setBackgroundResource(R.color.icon_i);
            holder.mTextView.setText(mDataset.get(position).getSubtitle());

        } else if (mDataset.get(position).getType() == 4) {
            // ITEM
            item = holder.back_item;

            header_item = holder.header_item;
            header_text = holder.header_text;

            Calendar d = Calendar.getInstance();
            Calendar dB = Calendar.getInstance();
            d.setTimeInMillis(mDataset.get(position).getItem().getDate_create());
            SimpleDateFormat D = new SimpleDateFormat("EEE, MMM d, y");
            if (position <= 1) {
                header_text.setText(D.format(new Date(d.getTimeInMillis())));
                header_item.setVisibility(View.VISIBLE);
            } else {
                dB.setTimeInMillis(mDataset.get(position - 1).getItem().getDate_create());
                if (d.get(Calendar.YEAR) == dB.get(Calendar.YEAR) &&
                        d.get(Calendar.MONTH) == dB.get(Calendar.MONTH) &&
                        d.get(Calendar.DAY_OF_MONTH) == dB.get(Calendar.DAY_OF_MONTH)) {
                    header_item.setVisibility(View.GONE);
                } else {
                    header_text.setText(D.format(new Date(d.getTimeInMillis())));
                    header_item.setVisibility(View.VISIBLE);
                }
            }

            if(position == mDataset.size() - 1)
                holder.mView.setPadding((int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin) / 2, (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin) / 2, (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin) / 2, (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin) / 2);
            else
                holder.mView.setPadding((int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin)/2, (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin)/2, (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin)/2, 0);


            // TITLE
            title = holder.title;
            title.setText(mDataset.get(position).getItem().getTitle());

            // INFO
            subtitle = holder.subtitle;
            if (mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_NOTIME)) {
                subtitle.setText(Utils.getHour(mDataset.get(position).getItem().getDate_create()));
            }
            if (mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_TIME)) {
                if (mDataset.get(position).getItem().getDate_reminded() == 0)
                    subtitle.setText(mContext.getResources().getString(R.string.snoozed) + ": "+Utils.getDate(Long.parseLong(mDataset.get(position).getItem().getAlarm_info())));
                else if (mDataset.get(position).getItem().getDate_reminded() != 0)
                    subtitle.setText(mContext.getResources().getString(R.string.reminded) + ": "+Utils.getDate(mDataset.get(position).getItem().getDate_reminded()));
            }
            if (mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_BLUETOOTH) || mDataset.get(position).getItem().getAlarm().equals(Costants.ALARM_TYPE_WIFI)) {
                if (mDataset.get(position).getItem().getDate_reminded() == 0)
                    subtitle.setText(mContext.getResources().getString(R.string.snoozed) + ": "+mDataset.get(position).getItem().getAlarm_info().split("_")[1]);
                else if (mDataset.get(position).getItem().getDate_reminded() != 0)
                    subtitle.setText(mContext.getResources().getString(R.string.reminded) + ": "+mDataset.get(position).getItem().getAlarm_info().split("_")[1]);
            }

            // ICON
            icon = holder.icon;
            back_icon = holder.back_icon;

            switch (mDataset.get(position).getItem().getType()) {
                case "PLACE":
                    icon.setImageResource(R.drawable.room_icon);
                    back_icon.setBackgroundResource(R.drawable.iconb_r);
                    break;
                case "HOME":
                    icon.setImageResource(R.drawable.ic_action_home);
                    back_icon.setBackgroundResource(R.drawable.iconb_r);
                    break;
                case "WORK":
                    icon.setImageResource(R.drawable.work_icon);
                    back_icon.setBackgroundResource(R.drawable.iconb_r);
                    break;
                case "LIST":
                    icon.setImageResource(R.drawable.icon_note);
                    back_icon.setBackgroundResource(R.drawable.iconb_do);
                    break;
                case "TODO":
                    icon.setImageResource(R.drawable.icon_note);
                    back_icon.setBackgroundResource(R.drawable.iconb_b);
                    break;
                case "FIXED":
                    icon.setImageResource(R.drawable.icon_note);
                    back_icon.setBackgroundResource(R.drawable.iconb_b);
                    break;
                case "DEVICE":
                    icon.setImageResource(R.drawable.icon_bluetooth_reminder);
                    back_icon.setBackgroundResource(R.drawable.iconb_t);
                    break;
                case "WIFI":
                    icon.setImageResource(R.drawable.icon_wifi_reminder);
                    back_icon.setBackgroundResource(R.drawable.iconb_t);
                    break;
                default:
                    icon.setImageResource(R.drawable.ic_action_bookmark_icon);
                    back_icon.setBackgroundResource(R.drawable.unchecked_icon);
                    break;
            }

            // SELECTED
            if (mDataset.get(position).getItem().Checked()) {
                back_icon.setBackgroundResource(R.drawable.checked_icon);
            }

            if (mDataset.get(position).isSelected()) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }

            final Intent intent = new Intent(mContext, AddItem.class);
            intent.setAction("EDIT");
            intent.putExtra(Costants.EXTRA_REMINDER, mDataset.get(position).getItem());
            /*item.setTransitionName("item");
            final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Main) mContext,
                    Pair.create((View) item, "item"));*/

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (!SP.getBoolean("enable_slide_item", true) || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                holder.controller.setVisibility(View.VISIBLE);

                // ON CLICK
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getSelectedItemCount() > 0) {
                            toggleSelection(position);
                        }
                    }
                });

                // Manage Controller
                holder.controller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (getSelectedItemCount() == 0) {
                            PopupMenu popup = new PopupMenu(mContext, v);
                            popup.inflate(R.menu.menu_popup_item);
                            if (!mDataset.get(position).getItem().Checked())
                                popup.getMenu().getItem(0).setTitle(mContext.getResources().getString(R.string.action_check));
                            else
                                popup.getMenu().getItem(0).setTitle(mContext.getResources().getString(R.string.action_uncheck));

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_check:
                                            if (!mDataset.get(position).getItem().Checked()) {
                                                ReminderService.startAction(mContext, Costants.ACTION_CHECKED, mDataset.get(position).getItem());
                                            } else {
                                                ReminderService.startAction(mContext, Costants.ACTION_UNCHECKED, mDataset.get(position).getItem());
                                            }
                                            return true;
                                        case R.id.action_modify:
                                            ((Main) mContext).startActivityForResult(intent, 2);
                                            //new AddItemDialog(mContext, intent).show();
                                            return true;
                                        case R.id.action_delete:
                                            new AlertDialog.Builder(mContext)
                                                    .setMessage(mContext.getResources().getString(R.string.ask_delete_reminder) + "?")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            ReminderService.startAction(mContext, Costants.ACTION_DELETE, mDataset.get(position).getItem());
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, null).show();
                                            return true;
                                    }
                                    return false;
                                }
                            });
                            popup.show();
                        }
                    }
                });

                // SWIPE TO CHECK
                if (!mDataset.get(position).getItem().Checked()) {
                    item.setOnTouchListener(null);
                }

            } else {

                holder.controller.setVisibility(View.GONE);

                // ON CLICK
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getSelectedItemCount() > 0) {
                            toggleSelection(position);
                        } else {
                            ((Main) mContext).startActivityForResult(intent, 2);
                            //new AddItemDialog(mContext, intent).show();
                        }
                    }
                });

                // SWIPE TO CHECK
                if (!mDataset.get(position).getItem().Checked()) {
                    item.setOnTouchListener(new SwipeDismissTouchListener(item, null,
                            new SwipeDismissTouchListener.DismissCallbacks() {
                                public void onDismiss(View view, Object token) {
                                    ReminderService.startAction(mContext, Costants.ACTION_CHECKED, mDataset.get(position).getItem());
                                }

                                public boolean canDismiss(Object token) {
                                    return true;
                                }
                            }));
                } else {
                    item.setOnTouchListener(null);
                }
            }

            // ON LONG CLICK
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    toggleSelection(position);
                    return true;
                }
            });

            // ON CLICK ICON
            back_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(position);
                }
            });
        }

        // SUGGESTION
        if (mDataset.get(position).getType() == 7) {
            // ITEM

            if (mDataset.get(position - 1).getType() == 0)
                holder.suggestion_back.setBackgroundResource(R.drawable.top_card);
            if (position == getItemCount() - 1)
                holder.suggestion_back.setBackgroundResource(R.drawable.bottom_card);

            // TITLE
            holder.suggestion_title.setText(mDataset.get(position).getSubtitle());

            // ON CLICK
            holder.suggestion_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AddItem.class);
                    intent.setAction("SUGGESTED");
                    intent.putExtra("suggestion", mDataset.get(position).getSubtitle());
                    ((Main) mContext).startActivityForResult(intent, 1);
                }
            });
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

    public void toggleSelection(int pos) {
        mDataset.get(pos).toggleSelected();
        notifyItemChanged(pos);
        ((Main)mContext).toggleCab(true);
    }

    public void clearSelections() {
        for(int k=0;k<mDataset.size();k++)
            if (mDataset.get(k).getType() == 4 && mDataset.get(k).isSelected())
                toggleSelection(k);
        ((Main)mContext).toggleCab(false);
    }

    public int getSelectedItemCount() {
        int f = 0;
        for(int k=0;k<mDataset.size();k++)
            if (mDataset.get(k).getType() == 4 && mDataset.get(k).isSelected())
                f++;
        return f;
    }

    public void selectAll() {
        if (getSelectedItemCount() != getItemCount() - 1) {
            for (int k = 0; k < mDataset.size(); k++)
                if (mDataset.get(k).getType() == 4 && !mDataset.get(k).isSelected())
                    toggleSelection(k);
        } else {
            clearSelections();
        }
        ((Main)mContext).toggleCab(true);
    }

    public ArrayList<Reminder> getSelectedItem() {
        ArrayList<Reminder> selected = new ArrayList<>();
        for (int k=0;k<mDataset.size();k++)
            if(mDataset.get(k).getType() == 4 && mDataset.get(k).isSelected()) {
                selected.add(mDataset.get(k).getItem());
            }
        return selected;
    }

    // GENERATE LIST
    public void generate_list(DbAdapter dbHelper, String query) {
        mDataset.clear();
        Cursor cursor;
        if (query.equals("NULL")) {
            int count = dbHelper.fetchAllReminders().getCount();
            if (count == 0) {
                no_item();
            } else {
                mDataset.add(new Item(1));

                cursor = dbHelper.fetchRemindersByFilterTodo();
                while (cursor.moveToNext())
                    mDataset.add(new Item(4, new Reminder(cursor)));
                cursor.close();
            }
        } else {
            int count = dbHelper.fetchRemindersByFilterTitle(query).getCount();
            if (count == 0) {
                mDataset.add(new Item(1));
                mDataset.add(new Item(0));
            } else {
                mDataset.add(new Item(1));

                cursor = dbHelper.fetchRemindersByFilterTitle(query);
                while (cursor.moveToNext())
                    mDataset.add(new Item(4, new Reminder(cursor)));
                cursor.close();
            }
        }
    }

    public void no_item() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (SP.getBoolean("enable_suggestions", false)) {
            ArrayList<String> list = Utils.suggestionRandom(mContext, 4);
            mDataset.add(new Item(1));
            mDataset.add(new Item(0));

            for (int random = 0; random < list.size(); random++)
                mDataset.add(new Item(7, list.get(random)));
        } else {
            mDataset.add(new Item(1));
            mDataset.add(new Item(0));
        }
    }

    public int update(String action, Reminder r, String query) {
        int k;

        boolean search = !query.equals("");

        if (mDataset.get(1).getType() == 0) {
            int tot = mDataset.size();
            mDataset.clear();
            notifyItemRangeRemoved(0, tot);
            mDataset.add(new Item(1));
            notifyItemInserted(0);
        }

        switch (action) {
            case Costants.ACTION_CREATE:
                if (search || r.getAlarm().equals(Costants.ALARM_TYPE_NOTIME)) {
                    if (!mDataset.contains(new Item(4, r))) {
                        mDataset.add(1, new Item(4, r));
                        notifyItemInserted(1);
                        notifyDataSetChangedCustom();
                    }
                }
                break;
            case Costants.ACTION_UPDATE:
                for (k = 1; k < mDataset.size(); k++) {
                    if (mDataset.get(k).getItem().getId() == r.getId()) {
                        if (search || r.getDate_reminded() != 0) {
                            mDataset.set(k, new Item(4, r));
                            notifyItemChanged(k);
                        } else {
                            mDataset.remove(k);
                            notifyItemRemoved(k);
                            notifyDataSetChangedCustom();
                        }
                        break;
                    }
                }
                break;
            case Costants.ACTION_REMINDED:
                if (!mDataset.contains(new Item(4, r))) {
                    for (k = 1; k < mDataset.size(); k++) {
                        if (search) {
                            if (mDataset.get(k).getItem().getId() == r.getId()) {
                                mDataset.set(k, new Item(4, r));
                                notifyItemChanged(k);
                                break;
                            }
                        } else {
                            if (mDataset.get(k).getItem().getDate_reminded() < r.getDate_reminded()) {
                                mDataset.add(k, new Item(4, r));
                                notifyItemInserted(k);
                                notifyDataSetChangedCustom();
                                break;
                            }
                        }
                    }
                    if (!search && k == mDataset.size()) {
                        mDataset.add(new Item(4, r));
                        notifyItemInserted(k + 1);
                        notifyDataSetChangedCustom();
                    }
                }
                break;
            case Costants.ACTION_DELETE:
                for (k=1;k<mDataset.size();k++) {
                    if (mDataset.get(k).getItem().getId() == r.getId()) {
                        mDataset.remove(k);
                        notifyItemRemoved(k);
                        notifyDataSetChangedCustom();
                        break;
                    }
                }
                break;
            case Costants.ACTION_UNDELETE:
                for (k=1;k<mDataset.size();k++) {
                    if (mDataset.get(k).getItem().getDate_reminded() < r.getDate_reminded()) {
                        mDataset.add(k, new Item(4, r));
                        notifyItemInserted(k);
                        notifyDataSetChangedCustom();
                        break;
                    }
                }
                if (k == mDataset.size()) {
                    mDataset.add(new Item(4, r));
                    notifyItemInserted(k + 1);
                    notifyDataSetChangedCustom();
                }
                break;
            case Costants.ACTION_CHECKED:
                for (k=1;k<mDataset.size();k++) {
                    if (mDataset.get(k).getItem().getId() == r.getId()) {
                        if (search) {
                            mDataset.set(k, new Item(4, r));
                            notifyItemChanged(k);
                        } else {
                            mDataset.remove(k);
                            notifyItemRemoved(k);
                            notifyDataSetChangedCustom();
                        }
                        break;
                    }
                }
                break;
            case Costants.ACTION_UNCHECKED:
                for (k=1;k<mDataset.size();k++) {
                    if (search) {
                        if (mDataset.get(k).getItem().getId() == r.getId()) {
                            mDataset.set(k, new Item(4, r));
                            notifyItemChanged(k);
                            break;
                        }
                    } else {
                        if (mDataset.get(k).getItem().getDate_reminded() < r.getDate_reminded()) {
                            mDataset.add(k, new Item(4, r));
                            notifyItemInserted(k);
                            notifyDataSetChangedCustom();
                            break;
                        }
                    }
                }
                if (!search && k == mDataset.size()) {
                    mDataset.add(new Item(4, r));
                    notifyItemInserted(k + 1);
                    notifyDataSetChangedCustom();
                }
                break;
        }
        ((Main) mContext).toggleCab(false);

        if (mDataset.size() == 1) {
            mDataset.clear();
            notifyItemRemoved(0);
            no_item();
            return 0;
        } else {
            return getItemCount() - 1;
        }

    }


    public void notifyDataSetChangedCustom() {
        //notifyItemRangeChanged(1, getItemCount());
    }
}