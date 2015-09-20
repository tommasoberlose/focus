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
import android.graphics.PorterDuff;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;
import android.widget.RelativeLayout;
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


public class Main extends AppCompatActivity {

    public RecyclerView listDrawer;
    public Toolbar toolbar;
    private boolean controlsVisible = true;
    private boolean search = false;
    private ActionMode mActionMode;

    private DrawerLayout drawerLayout;
    public MyAdapterD drawerAdapter;

    private String query = "";
    private String message = "";

    private String FragmentA = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.tab_intro(this);

        // TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(true);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        drawerLayout.setDrawerShadow(android.R.color.transparent, Gravity.RIGHT);
        mDrawerToggle.syncState();

        // INTENT
        Intent intent = getIntent();

        //SEARCH
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search = true;
            query = intent.getStringExtra(SearchManager.QUERY);
            if (getFragmentA().equals("R")) {
                ((MainFragment) getFragmentManager().findFragmentById(R.id.viewer)).update_list(query);
            }
        } else if (Costants.EXTRA_ACTION_EDIT.equals(intent.getAction())) {
            Intent i = new Intent(Main.this, AddItem.class);
            i.setAction(Costants.EXTRA_ACTION_EDIT);
            i.putExtra(Costants.EXTRA_REMINDER, intent.getParcelableExtra(Costants.EXTRA_REMINDER));
            startActivityForResult(i, 2);
        } else if ("FIXED".equals(intent.getAction())) {
            Intent i = new Intent(Main.this, AddItem.class);
            i.setAction("FIXED");
            i.putExtra(Costants.EXTRA_REMINDER, intent.getParcelableExtra(Costants.EXTRA_REMINDER));
            startActivityForResult(i, 2);
        } else if (Costants.EXTRA_ACTION_ADD.equals(intent.getAction())) {
            startActivityForResult(new Intent(this, AddItem.class), 1);
        }

        listDrawer = (RecyclerView) findViewById(R.id.drawerList);
        listDrawer.setHasFixedSize(true);
        LinearLayoutManager llm1 = new LinearLayoutManager(getBaseContext());
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        listDrawer.setLayoutManager(llm1);

        drawer_menu(listDrawer, Utils.itemsToDo(this));

        if (getFragmentByA() == null)
            changeD("R");
        else
            changeD(getFragmentA());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            if (!search)
                toggleDrawer(Gravity.LEFT);
            else
                onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen(Gravity.LEFT)) {
            toggleDrawer(Gravity.LEFT);
        } else {
            switch (getFragmentA()) {
                case "R":
                    if (!query.equals("")) {
                        query = "";
                        ((MainFragment) getFragmentManager().findFragmentById(R.id.viewer)).update_list(query);
                        invalidateOptionsMenu();
                    } else {
                        super.onBackPressed();
                    }
                    break;
                default:
                    super.onBackPressed();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {
            Utils.tab_intro(this);
            if (getFragmentA().equals("R"))
                ((MainFragment) getFragmentManager().findFragmentById(R.id.viewer)).update_list(query);
        }
    }

    public void slide_hDown(FloatingActionButton button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.animate().setDuration(200).translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            if (button != null)
                button.animate().setDuration(200).translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            controlsVisible = true;
        }
    }

    // TOOLBAR SCROLL
    public void slide_h(RecyclerView recyclerView, int dy, FloatingActionButton button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0)
                toolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation_i));
            else
                toolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation_scrolled));


            if (dy < 0) {
                if (!controlsVisible) {
                    slide_hDown(button);
                    controlsVisible = true;
                }
            } else {
                if (controlsVisible && ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() != 0) {
                    toolbar.animate().setDuration(200).translationY(-toolbar.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    if (button != null)
                        button.animate().setDuration(200).translationY(button.getHeight() * 2).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    controlsVisible = false;
                }
            }
        }
    }


    // DRAWER MENU
    public void drawer_menu(RecyclerView l, int count) {
        ArrayList<String> list = new ArrayList<>();
        list.add("HEADER");
        list.add("R");
        list.add("T");
        //list.add("L");
        list.add("DIVIDER");
        list.add("S");
        list.add("F");
        drawerAdapter = new MyAdapterD(Main.this, count, list);
        l.setAdapter(drawerAdapter);
    }


    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            // SHARE
            if (id == R.id.action_share) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String toSend = "";
                ArrayList<Reminder> toSendR = ((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).adapter.getSelectedItem();
                for (int k = 0; k< toSendR.size(); k++) {
                    String g = "";
                    if (k != 0)
                        g = "\n";
                    toSend = toSend + g + toSendR.get(k).getTitle();
                }
                sendIntent.putExtra(Intent.EXTRA_TEXT, toSend);
                sendIntent.setType("text/plain");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Utils.SnackbarC(Main.this, getResources().getString(R.string.error), ((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).button);
                }
                return true;
            }

            //DELETE
            if(id == R.id.action_delete) {
                new AlertDialog.Builder(Main.this)
                        .setMessage(getResources().getString(R.string.ask_delete_reminder) + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ArrayList<Reminder> toUse = ((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).adapter.getSelectedItem();
                                if (((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).adapter.getSelectedItemCount() == 1) {
                                    ReminderService.startAction(Main.this, Costants.ACTION_DELETE, toUse.get(0));
                                } else {
                                    ReminderService.startAction(Main.this, Costants.ACTION_DELETE_MULTI, toUse);
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }

            // SELECT ALL
            if(id == R.id.action_select_all) {
                ((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).adapter.selectAll();
                return true;
            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
            }
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mode.getMenuInflater().inflate(R.menu.menu_context, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
            }
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).adapter.clearSelections();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }

    public void toggleCab(boolean open) {
        if (getFragmentA().equals("R")) {
            if (open) {
                if (mActionMode == null)
                    mActionMode = Main.this.startSupportActionMode(new ActionBarCallBack());

                if (((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).adapter.getSelectedItemCount() != 0)
                    mActionMode.setTitle("" + ((MainFragment)getFragmentManager().findFragmentById(R.id.viewer)).adapter.getSelectedItemCount());
                else {
                    mActionMode.finish();
                    mActionMode = null;
                }
            } else {
                if (mActionMode != null)
                    mActionMode.finish();
                mActionMode = null;
            }
        }
    }

    public void toggleDrawer(int g) {
        if (!isDrawerOpen(g))
            drawerLayout.openDrawer(g);
        else
            drawerLayout.closeDrawer(g);
    }

    public boolean isDrawerOpen(int g) {
        return drawerLayout.isDrawerOpen(g);
    }

    public void changeD(String section) {

        switch (section) {
            case "R":

                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).replace(R.id.viewer, new MainFragment()).addToBackStack(null).commit();

                setTitle(R.string.title_activity_main);

                toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
                toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
                toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

                break;
            case "T":
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).replace(R.id.viewer, new TimelineFragment()).addToBackStack(null).commit();

                setTitle(R.string.title_activity_timeline);

                toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text));
                toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
                drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.white_dark));

                toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.secondary_text), PorterDuff.Mode.SRC_ATOP);

                break;
            case "L":

                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).replace(R.id.viewer, new LocationFragment()).addToBackStack(null).commit();

                setTitle(R.string.title_activity_location);

                toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
                toolbar.setBackgroundColor(getResources().getColor(R.color.primary_red));
                drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_red_dark));

                toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

                break;
        }

        FragmentA = section;
        drawerAdapter.selectItem(section);
        slide_hDown(null);

        invalidateOptionsMenu();
    }

    public String getFragmentA() {
        return FragmentA;
    }

    public Fragment getFragmentByA() {
        switch(getFragmentA()) {
            case "R":
                return new MainFragment();
            case "T":
                return new TimelineFragment();
            case "L":
                return new LocationFragment();
        }
        return null;
    }

}
