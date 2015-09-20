package nego.reminders;


import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import android.os.Handler;

import nego.reminders.Adapter.MyAdapter;
import nego.reminders.Adapter.MyAdapterD;
import nego.reminders.Adapter.MyAdapterP;
import nego.reminders.database.DbAdapter;
import nego.reminders.database.DbAdapterP;

public class LocationFragment extends Fragment {


    private RecyclerView recList;
    public FloatingActionButton button;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private MyAdapterP adapter;
    private DbAdapterP dbHelper;

    private Place p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        setHasOptionsMenu(true);

        button = (FloatingActionButton) rootView.findViewById(R.id.fab_1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View textEntryView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_place, null);
                final AlertDialog aB = new AlertDialog.Builder(getActivity())
                        .setView(textEntryView)
                        .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                aB.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText name = (EditText) textEntryView.findViewById(R.id.name);
                        EditText address = (EditText) textEntryView.findViewById(R.id.address);

                        if (Utils.isEmpty(name)) {
                            ((android.support.design.widget.TextInputLayout) name.getParent()).setError(getResources().getString(R.string.error_name));
                        } else if (Utils.isEmpty(address)) {
                            ((android.support.design.widget.TextInputLayout) address.getParent()).setError(getResources().getString(R.string.error_address));
                        } else {
                            p = new Place(name.getText().toString(), "", "", "");
                            if (p.getName().equals(getResources().getString(R.string.home)))
                                p.setInfo(getResources().getString(R.string.home));
                            else if (p.getName().equals(getResources().getString(R.string.work)))
                                p.setInfo(getResources().getString(R.string.work));
                            else
                                p.setInfo("PLACE");

                            Address add = Utils.getLocationFromAddress(getActivity(), address.getText().toString());
                            p.setLatitude("" + add.getLatitude());
                            p.setLongitude("" + add.getLongitude());
                            if (save_place(p) > 0) {
                                update_list();
                            }
                            aB.dismiss();
                        }
                    }
                });
            }
        });

        // RECYCLER LIST
        recList = (RecyclerView) rootView.findViewById(R.id.listView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        recList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ((Main) getActivity()).slide_h(recyclerView, dy, button);
            }
        });

        final TypedValue typed_value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recList.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        int cx = recList.getWidth() / 2;
                        int cy = getResources().getDimensionPixelSize(typed_value.resourceId) / 2;
                        int finalRadius = recList.getHeight();
                        Animator anim =
                                ViewAnimationUtils.createCircularReveal(recList, cx, cy, 0, finalRadius);
                        anim.setDuration(600);
                        update_list();
                        recList.setAlpha(1);
                        anim.start();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_red);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId) / 2);

        update_list();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_location, menu);
    }

    // UPDATE LIST
    public MyAdapterP update_list(DbAdapterP dbHelper) {
        MyAdapterP mAdapter;
        mAdapter = new MyAdapterP(dbHelper, getActivity());
        return mAdapter;
    }

    // SAVE PLACE
    public long save_place(Place pl) {
        dbHelper = new DbAdapterP(getActivity());
        dbHelper.open();
        long done = dbHelper.createPlace(pl);
        dbHelper.close();
        return done;
    }

    // DELETE PLACE
    public boolean delete_place(String pl) {

        DbAdapter dbHelper1 = new DbAdapter(getActivity());
        dbHelper1.open();
        Cursor c = dbHelper1.fetchAllReminders();

        while (c.moveToNext()) {
            Reminder r = new Reminder(c);
            if (r.getAlarm().equals(Costants.ALARM_TYPE_WHERE) && r.getAlarm_info().equals(pl)) {
                r.setAlarm(Costants.ALARM_TYPE_NOTIME);
                r.setAlarm_info("");
                r.setType("TODO");
                r.update_reminder(getActivity(), dbHelper1);
            }
        }
        c.close();

        dbHelper1.close();

        dbHelper = new DbAdapterP(getActivity());
        dbHelper.open();
        boolean done = dbHelper.deletePlace(pl);
        dbHelper.close();

        return done;
    }

    public void update_list() {

        mSwipeRefreshLayout.setRefreshing(true);

        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                dbHelper = new DbAdapterP(getActivity());
                dbHelper.open();
                adapter = update_list(dbHelper);
                mHandler.post(new Runnable() {
                    public void run() {
                        recList.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                        ((Main) getActivity()).slide_hDown(button);
                    }
                });

                dbHelper.close();
            }
        }).start();
    }

}
