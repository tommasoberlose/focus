package nego.reminders.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import nego.reminders.LocationFragment;
import nego.reminders.Main;
import nego.reminders.Place;
import nego.reminders.R;
import nego.reminders.Utils;
import nego.reminders.database.DbAdapterP;


public class MyAdapterP extends RecyclerView.Adapter<MyAdapterP.ViewHolder> {
    private List<Place> mDataset = new ArrayList<Place>();
    private List<Address> mAddress = new ArrayList<>();
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout v;
        public LinearLayout back_item;
        public TextView no_place;
        public TextView title;
        public TextView subtitle;
        public ViewHolder(LinearLayout v, LinearLayout back_item, TextView no_place, TextView title, TextView subtitle) {
            super(v);
            this.v = v;
            this.back_item = back_item;
            this.no_place = no_place;
            this.title = title;
            this.subtitle = subtitle;
        }

    }

    public MyAdapterP(DbAdapterP dbHelper, Context mContext) {
        this.mContext = mContext;
        generate_list(dbHelper);
    }

    @Override
    public MyAdapterP.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {

        ViewHolder vh;
        View v;

        v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_place, parent, false);

        vh = new ViewHolder((LinearLayout) v, (LinearLayout) v.findViewById(R.id.back_item), (TextView) v.findViewById(R.id.no_place), (TextView) v.findViewById(R.id.title), (TextView) v.findViewById(R.id.subtitle));
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (mDataset.get(position).getName().equals("")) {
            holder.back_item.setVisibility(View.GONE);
            holder.no_place.setVisibility(View.VISIBLE);
        } else {
            // TITLE
            TextView title = holder.title;
            title.setText(mDataset.get(position).getName());

            // INFO
            TextView subtitle = holder.subtitle;
            if (mAddress.get(position) != null) {
                subtitle.setText(mAddress.get(position).getAddressLine(0) + ", " + mAddress.get(position).getAddressLine(1));


                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final View textEntryView = LayoutInflater.from(mContext).inflate(R.layout.dialog_place, null);

                        final EditText name = (EditText) textEntryView.findViewById(R.id.name);
                        final EditText address = (EditText) textEntryView.findViewById(R.id.address);

                        name.setText(mDataset.get(position).getName());
                        address.setText(mAddress.get(position).getAddressLine(0) + ", " + mAddress.get(position).getAddressLine(1));

                        AlertDialog aB = new AlertDialog.Builder(mContext)
                                .setView(textEntryView)
                                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                })
                                .setNegativeButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final DialogInterface a = dialog;
                                        new AlertDialog.Builder(mContext)
                                                .setMessage(mContext.getResources().getString(R.string.ask_delete_place))
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        if (((LocationFragment) ((Main) mContext).getFragmentManager().findFragmentById(R.id.viewer)).delete_place("" + mDataset.get(position).getId())) {
                                                            ((LocationFragment) ((Main) mContext).getFragmentManager().findFragmentById(R.id.viewer)).update_list();
                                                        }
                                                        a.dismiss();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, null).show();
                                    }
                                }).show();
                        aB.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                name.setText(mDataset.get(position).getName());
                                address.setText(mAddress.get(position).getAddressLine(0));

                                if (Utils.isEmpty(name)) {
                                    ((android.support.design.widget.TextInputLayout) name.getParent()).setError(mContext.getResources().getString(R.string.error_name));
                                } else if (Utils.isEmpty(address)) {
                                    ((android.support.design.widget.TextInputLayout) address.getParent()).setError(mContext.getResources().getString(R.string.error_address));
                                } else {
                                    Place p = new Place(name.getText().toString(), "", "", "");
                                    if (p.getName().equals(mContext.getResources().getString(R.string.home)))
                                        p.setInfo(mContext.getResources().getString(R.string.home));
                                    else if (p.getName().equals(mContext.getResources().getString(R.string.work)))
                                        p.setInfo(mContext.getResources().getString(R.string.work));
                                    else
                                        p.setInfo("GENERAL");

                                    p.setLatitude("" + mAddress.get(position).getLatitude());
                                    p.setLongitude("" + mAddress.get(position).getLongitude());
                                    if (((LocationFragment) ((Main) mContext).getFragmentManager().findFragmentById(R.id.viewer)).save_place(p) > 0) {
                                        ((LocationFragment) ((Main) mContext).getFragmentManager().findFragmentById(R.id.viewer)).update_list();
                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                subtitle.setText(mContext.getResources().getString(R.string.error_undefined));
                holder.v.setOnClickListener(null);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // GENERATE LIST
    public void generate_list(DbAdapterP dbHelper) {
        mDataset.clear();
        Cursor cursor;
        int count = dbHelper.getPlacesN();
        if (count == 0) {
            mDataset.add(new Place("", "", "", ""));
        } else {
            cursor = dbHelper.fetchAllPlaces();
            while (cursor.moveToNext()) {
                mDataset.add(new Place(cursor));
                mAddress.add(Utils.getAddressFromLocation(mContext, mDataset.get(mDataset.size() - 1).getLatitude(), mDataset.get(mDataset.size() -1).getLongitude()));
            }
            cursor.close();
        }
    }

}