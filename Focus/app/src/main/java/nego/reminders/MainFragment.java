package nego.reminders;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import nego.reminders.Adapter.MyAdapter;
import nego.reminders.Adapter.MyAdapterD;
import nego.reminders.Functions.LocationF;
import nego.reminders.Functions.ReminderF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.Slide.SecondSlide;
import nego.reminders.database.DbAdapter;


public class MainFragment extends Fragment {

    private RecyclerView recList;
    public Toolbar toolbar;
    public FloatingActionButton button;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String query = "";
    private String message = "";
    public SearchView searchView;

    public MyAdapter adapter;
    private Menu menu;

    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        registerForContextMenu(rootView);

        // FLOATING BUTTON
        button = (FloatingActionButton) rootView.findViewById(R.id.fab_1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddItem.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), button, "floating_button");
                startActivityForResult(intent, 1, options.toBundle());
            }
        });

        // RECYCLER LIST
        recList = (RecyclerView) rootView.findViewById(R.id.listView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
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
                        int cy = getResources().getDimensionPixelSize(typed_value.resourceId) * 7 / 4;
                        int finalRadius = recList.getHeight();
                        Animator anim =
                                ViewAnimationUtils.createCircularReveal(recList, cx, cy, 0, finalRadius);
                        anim.setDuration(600);
                        update_list(query);
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
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId) * 3 / 2);

        update_list(query);

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        if (adapter != null && adapter.getSelectedItemCount() == 0) {

            SearchManager searchManager = (SearchManager)
                    getActivity().getSystemService(Context.SEARCH_SERVICE);
            MenuItem searchMenuItem = menu.findItem(R.id.action_search);
            if (searchMenuItem != null) {
                searchView = (SearchView) searchMenuItem.getActionView();

                searchView.setSearchableInfo(searchManager.
                        getSearchableInfo(getActivity().getComponentName()));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        query = s;
                        update_list(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        query = s;
                        update_list(query);
                        return false;
                    }
                });

                MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        update_list(query);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        update_list(query);
                        getActivity().invalidateOptionsMenu();
                        return true;
                    }
                });
            }

        }
        this.menu = menu;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(Costants.ACTION_UPDATE_LIST);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action_type = intent.getStringExtra(Costants.EXTRA_ACTION_TYPE);
                int count = adapter.getItemCount() - 1;
                switch (action_type) {
                    case Costants.ACTION_CREATE:
                        count = adapter.update(action_type, (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), query);
                        break;
                    case Costants.ACTION_UPDATE:
                        count = adapter.update(action_type, (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), query);
                        break;
                    case Costants.ACTION_REMINDED:
                        count = adapter.update(action_type, (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), query);
                        break;
                    case Costants.ACTION_DELETE:
                        count = adapter.update(action_type, (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), query);
                        Utils.SnackbarC(getActivity(), getString(R.string.reminder_deleted), (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), Costants.ACTION_UNDELETE, button);
                        break;
                    case Costants.ACTION_UNDELETE:
                        count = adapter.update(action_type, (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), query);
                        Utils.SnackbarC(getActivity(), getString(R.string.reminder_restored), button);
                        break;
                    case Costants.ACTION_DELETE_MULTI:
                        ArrayList<Reminder> RM = intent.getParcelableArrayListExtra(Costants.EXTRA_REMINDER);
                        update_list(query);
                        Utils.SnackbarC(getActivity(), RM.size() + " " + getString(R.string.reminders_deleted), RM,  Costants.ACTION_UNDELETE_MULTI, button);
                        break;
                    case Costants.ACTION_UNDELETE_MULTI:
                        ArrayList<Reminder> RM2 = intent.getParcelableArrayListExtra(Costants.EXTRA_REMINDER);
                        update_list(query);
                        Utils.SnackbarC(getActivity(), RM2.size() + " " + getString(R.string.reminders_restored), button);
                        break;
                    case Costants.ACTION_CHECKED:
                        count = adapter.update(action_type, (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), query);
                        Utils.SnackbarC(getActivity(), getString(R.string.reminder_checked), (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), Costants.ACTION_UNCHECKED, button);
                        break;
                    case Costants.ACTION_UNCHECKED:
                        count = adapter.update(action_type, (Reminder) intent.getParcelableExtra(Costants.EXTRA_REMINDER), query);
                        Utils.SnackbarC(getActivity(), getString(R.string.reminder_unchecked), button);
                        break;
                }
                ((Main)getActivity()).drawerAdapter.changeCount(count);
                ((Main)getActivity()).slide_hDown(button);
                //adapter.notifyDataSetChanged();
                update_list(query);
            }
        };
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {
            update_list("");
        }
    }

    // UPDATE LIST
    public void update_list(final String query) {

        mSwipeRefreshLayout.setRefreshing(true);

        this.query = query;
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                DbAdapter dbHelper = new DbAdapter(getActivity());
                dbHelper.open();

                final MyAdapter mAdapter;
                if (query.equals(""))
                    mAdapter = new MyAdapter(dbHelper, "NULL", getActivity());
                else
                    mAdapter = new MyAdapter(dbHelper, query, getActivity());

                dbHelper.close();

                mHandler.post(new Runnable() {
                    public void run() {
                        recList.setAdapter(mAdapter);
                        adapter = mAdapter;
                        adapter.clearSelections();
                        mSwipeRefreshLayout.setRefreshing(false);

                        ((Main) getActivity()).toggleCab(false);
                        ((Main) getActivity()).slide_hDown(button);
                    }
                });
            }
        }).start();
    }

}
