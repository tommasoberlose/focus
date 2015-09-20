package nego.reminders.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import nego.reminders.Main;
import nego.reminders.R;
import nego.reminders.SettingsActivity;
import nego.reminders.Utils;


public class MyAdapterD extends RecyclerView.Adapter<MyAdapterD.ViewHolder> {
    private List<String> mDataset = new ArrayList<String>();
    private Context mContext;
    private int count;
    private String itemselected = "R";


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout v;
        public ViewHolder(LinearLayout v) {
            super(v);
            this.v = v;
        }

        public TextView firstRow;
        public TextView secondRow;
        public ImageView imgA;
        public ViewHolder(LinearLayout v, TextView firstRow, TextView secondRow, ImageView imgA) {
            super(v);
            this.v = v;
            this.firstRow = firstRow;
            this.secondRow = secondRow;
            this.imgA = imgA;
        }

        public TextView title;
        public ImageView icon;
        public ViewHolder(LinearLayout v, TextView title, ImageView icon) {
            super(v);
            this.v = v;
            this.title = title;
            this.icon = icon;
        }

    }

    public MyAdapterD(Context mContext, int count, ArrayList<String> l) {
        mDataset = l;
        this.mContext = mContext;
        this.count = count;
    }

    @Override
    public MyAdapterD.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {

        ViewHolder vh;
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_header, parent, false);
            vh = new ViewHolder((LinearLayout) v, (TextView) v.findViewById(R.id.firstRow), (TextView) v.findViewById(R.id.secondRow), (ImageView) v.findViewById(R.id.imgA));
        } else if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_divider, parent, false);
            vh = new ViewHolder((LinearLayout) v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_item, parent, false);
            vh = new ViewHolder((LinearLayout) v, (TextView) v.findViewById(R.id.title_text), (ImageView) v.findViewById(R.id.icon));
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (mDataset.get(position).equals("HEADER")) {

            TextView firstRow = holder.firstRow;
            TextView secondRow = holder.secondRow;
            ImageView imgA = holder.imgA;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Cursor c = mContext.getContentResolver().query(
                        ContactsContract.Profile.CONTENT_URI, null, null, null, null);
                String[] columnNames = c.getColumnNames();
                c.moveToFirst();
                for (int j = 0; j < columnNames.length; j++) {
                    String columnName = columnNames[j];
                    if (j == 0)
                        firstRow.setText(c.getString(c.getColumnIndex(columnName)));
                }
                c.close();
            }

            String count_text = count + "";
            if (count > 99)
                count_text = "99+";
            secondRow.setText(count_text);

        } else if (mDataset.get(position).equals("DIVIDER")) {

        } else {
            // TITLE
            TextView title = holder.title;
            ImageView icon = holder.icon;

            title.setTextColor(mContext.getResources().getColor(R.color.all_text));
            icon.setColorFilter(mContext.getResources().getColor(R.color.secondary_text));

            if (mDataset.get(position).equals("R")) {
                title.setText(mContext.getResources().getString(R.string.title_activity_main));
                icon.setImageResource(R.drawable.ic_action_bookmark_icon);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!((Main) mContext).getFragmentA().equals(mDataset.get(position))) {
                            ((Main) mContext).changeD("R");
                            ((Main) mContext).toggleDrawer(Gravity.LEFT);
                        }
                    }
                });

                if (itemselected.equals(mDataset.get(position))) {
                    title.setTextColor(mContext.getResources().getColor(R.color.primary_dark));
                    icon.setColorFilter(mContext.getResources().getColor(R.color.primary_dark));
                    holder.v.setSelected(true);
                } else {
                    title.setTextColor(mContext.getResources().getColor(R.color.primary_text));
                    icon.setColorFilter(mContext.getResources().getColor(R.color.secondary_text));
                    holder.v.setSelected(false);
                }

            } else if (mDataset.get(position).equals("T")) {
                title.setText(mContext.getResources().getString(R.string.title_activity_timeline));
                icon.setImageResource(R.drawable.ic_action_today);
                icon.setColorFilter(mContext.getResources().getColor(R.color.icon_do));

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!((Main) mContext).getFragmentA().equals(mDataset.get(position))) {
                            ((Main) mContext).changeD("T");
                            ((Main) mContext).toggleDrawer(Gravity.LEFT);
                        }
                    }
                });

                if (itemselected.equals(mDataset.get(position))) {
                    title.setTextColor(mContext.getResources().getColor(R.color.accent_1));
                    icon.setColorFilter(mContext.getResources().getColor(R.color.accent_1));
                    holder.v.setSelected(true);
                } else {
                    title.setTextColor(mContext.getResources().getColor(R.color.primary_text));
                    icon.setColorFilter(mContext.getResources().getColor(R.color.secondary_text));
                    holder.v.setSelected(false);
                }
            } else if (mDataset.get(position).equals("L")) {
                title.setText(mContext.getResources().getString(R.string.title_activity_location));
                icon.setImageResource(R.drawable.ic_action_room);
                icon.setColorFilter(mContext.getResources().getColor(R.color.primary_red_dark));

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!((Main) mContext).getFragmentA().equals(mDataset.get(position))) {
                            ((Main) mContext).changeD("L");
                            ((Main) mContext).toggleDrawer(Gravity.LEFT);
                        }
                    }
                });

                if (itemselected.equals(mDataset.get(position))) {
                    title.setTextColor(mContext.getResources().getColor(R.color.primary_red_dark));
                    icon.setColorFilter(mContext.getResources().getColor(R.color.primary_red_dark));
                    holder.v.setSelected(true);
                } else {
                    title.setTextColor(mContext.getResources().getColor(R.color.primary_text));
                    icon.setColorFilter(mContext.getResources().getColor(R.color.secondary_text));
                    holder.v.setSelected(false);
                }
            } else if (mDataset.get(position).equals("S")) {
                title.setText(mContext.getResources().getString(R.string.action_settings));
                icon.setImageResource(R.drawable.ic_action_settings);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Main) mContext).toggleDrawer(Gravity.LEFT);
                        Intent intent = new Intent(mContext, SettingsActivity.class);
                        ((Main) mContext).startActivityForResult(intent, 4);
                    }
                });
            } else if (mDataset.get(position).equals("F")) {
                title.setText(mContext.getResources().getString(R.string.help_feedback));
                icon.setImageResource(R.drawable.ic_communication_comment);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Main) mContext).toggleDrawer(Gravity.LEFT);
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"tommaso.berlose@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "FEEDBACK App Focus");
                        try {
                            ((Main) mContext).startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Utils.SnackbarC(mContext, "There are no email clients installed.", v);
                        }
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mDataset.get(position)) {
            case "HEADER":
                return 0;
            case "DIVIDER":
                return 1;
            default:
                return 2;
        }
    }

    public void changeCount(int count) {
        this.count = count;
        notifyItemChanged(0);
    }

    public void selectItem(String item) {
        itemselected = item;
        notifyItemChanged(1);
        notifyItemChanged(2);
        notifyItemChanged(3);
    }

}
