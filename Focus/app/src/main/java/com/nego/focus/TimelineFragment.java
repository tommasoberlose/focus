package com.nego.focus;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import nego.reminders.Adapter.MyAdapterTimeline;
import nego.reminders.database.DbAdapter;


public class TimelineFragment extends Fragment {


    private RecyclerView recList;

    private MyAdapterTimeline adapter;
    private DbAdapter dbHelper;
    public FloatingActionButton button;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        // FLOATING BUTTON
        button = (FloatingActionButton) rootView.findViewById(R.id.fab_1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddItem.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), button, "floating_button");
                    startActivityForResult(intent, 1, options.toBundle());
                } else {
                    startActivityForResult(intent, 1);
                }
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
                        update_list(true);
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
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId) * 3  / 2);

        update_list(true);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_timeline, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            update_list(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(Costants.ACTION_UPDATE_LIST);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action_type = intent.getStringExtra(Costants.EXTRA_ACTION_TYPE);
                Reminder k = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                switch (action_type) {
                    case Costants.ACTION_CREATE:
                        adapter.update(action_type, k);
                        break;
                    case Costants.ACTION_UPDATE:
                        adapter.update(action_type, k);
                        Utils.SnackbarC(getActivity(), getString(R.string.reminder_modified), button);
                        break;
                    case Costants.ACTION_REMINDED:
                        adapter.update(action_type, k);
                        break;
                    case Costants.ACTION_DELETE:
                        adapter.update(action_type, k);
                        Utils.SnackbarC(getActivity(), getString(R.string.reminder_deleted), k, Costants.ACTION_UNDELETE, button);
                        break;
                    case Costants.ACTION_UNDELETE:
                        adapter.update(action_type, k);
                        Utils.SnackbarC(getActivity(), getString(R.string.reminder_restored), button);
                        break;
                }
                ((Main)getActivity()).drawerAdapter.changeCount(Utils.itemsToDo(getActivity()));
                ((Main)getActivity()).slide_hDown(button);
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
    }

    // UPDATE LIST
    public void update_list(final boolean start) {

        mSwipeRefreshLayout.setRefreshing(true);
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                dbHelper = new DbAdapter(getActivity());
                dbHelper.open();
                final MyAdapterTimeline mAdapter = new MyAdapterTimeline(dbHelper, getActivity());
                dbHelper.close();

                mHandler.post(new Runnable() {
                    public void run() {
                        if (start) {
                            recList.setAdapter(mAdapter);
                            adapter = mAdapter;
                        } else {
                            recList.swapAdapter(mAdapter, true);
                            adapter = mAdapter;
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        ((Main) getActivity()).slide_hDown(button);
                    }
                });
            }
        }).start();
    }
}
