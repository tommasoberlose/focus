package nego.reminders;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import nego.reminders.Adapter.AdapterC;
import nego.reminders.Adapter.MyAdapter;
import nego.reminders.database.DbAdapter;


public class ChooseActivated extends AppCompatActivity {

    private RecyclerView recList;
    private Toolbar toolbar;

    private String what = Costants.ALARM_TYPE_BLUETOOTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_activated);

        // TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getAction().equals(".activities.ChooseActivated.action.BLUETOOTH")) {
            what = Costants.ALARM_TYPE_BLUETOOTH;
            setTitle(getString(R.string.active_device));
        } else {
            what = Costants.ALARM_TYPE_WIFI;
            setTitle(getString(R.string.active_connection));
        }

        // RECYCLER LIST
        recList = (RecyclerView) findViewById(R.id.listView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        AdapterC mAdapter = new AdapterC(what, ChooseActivated.this);

        recList.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_activated, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            if (what.equals(Costants.ALARM_TYPE_BLUETOOTH))
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            else
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
