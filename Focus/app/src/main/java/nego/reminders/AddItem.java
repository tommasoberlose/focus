package nego.reminders;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.opengl.Visibility;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import nego.reminders.Functions.AlarmF;
import nego.reminders.Functions.LocationF;
import nego.reminders.Functions.NotificationF;
import nego.reminders.Functions.ReminderF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.database.DbAdapter;
import nego.reminders.database.DbAdapterP;


public class AddItem extends AppCompatActivity {

    Reminder r, old;
    boolean edit = false;
    boolean changed = false;
    boolean check = false;

    private DbAdapter dbHelper;
    private Cursor cursor;

    private EditText title;
    private RelativeLayout toShow;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private String bluetoothName = "";
    private String bluetoothAdd = "";

    private String wifiName = "";
    private String wifiAdd = "";

    private boolean fixed = false;

    private Place place = null;
    private Place home = null;

    private String choose = Costants.ALARM_TYPE_NOTIME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title =((EditText) findViewById(R.id.editText));

        toShow = (RelativeLayout) findViewById(R.id.selected_reminder);

        resetCal();

        // ITEM
        Intent intent = getIntent();
        if(intent.getAction() != null && (intent.getAction().equals("EDIT") || intent.getAction().equals("FIXED"))) {


            edit = true;
            r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);

            if (intent.getAction().equals("FIXED")) {
                r.setType("TODO");
                dbHelper = new DbAdapter(AddItem.this);
                dbHelper.open();
                r.update_reminder(AddItem.this, dbHelper);
                dbHelper.close();
            }

            try {
                NotificationF.CancelNotification(AddItem.this, "" + r.getId());
                setTitle("");
                old = r;
                title.setText(r.getTitle());

                choose = r.getAlarm();
                switch (choose) {
                    case "NOTIME":
                        if (r.getType().equals("FIXED"))
                            fixed = true;
                        break;
                    case "TIME":
                        Calendar e = Calendar.getInstance();
                        e.setTimeInMillis(Long.parseLong(r.getAlarm_info()));
                        year = e.get(Calendar.YEAR);
                        month = e.get(Calendar.MONTH);
                        day = e.get(Calendar.DAY_OF_MONTH);
                        hour = e.get(Calendar.HOUR_OF_DAY);
                        minute = e.get(Calendar.MINUTE);
                        changeR(getResources().getString(R.string.snooze_until) + " " + getDate(), true);
                        break;
                    case "WHERE":
                        place = getPlace(r.getAlarm_info());
                        changeR(getResources().getString(R.string.snooze_until) + " " + place.getName(), true);
                        break;
                    case "BLUETOOTH":
                        bluetoothAdd = r.getAlarm_info().split("_")[0];
                        bluetoothName = r.getAlarm_info().split("_")[1];
                        changeR(getResources().getString(R.string.snooze_until) + " " + bluetoothName, true);
                        break;
                    case "WIFI":
                        wifiAdd = r.getAlarm_info().split("_")[0];
                        wifiName = r.getAlarm_info().split("_")[1];
                        changeR(getResources().getString(R.string.snooze_until) + " " + r.getAlarm_info().split("_")[1], true);
                        break;
                }
                if (r.Checked()) {
                    findViewById(R.id.action_uncheck).setVisibility(View.VISIBLE);
                    check = true;
                } else {
                    findViewById(R.id.action_check).setVisibility(View.VISIBLE);
                    check = false;
                }

                findViewById(R.id.action_check).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.action_uncheck).setTranslationZ(1);
                        v.setTranslationZ(0);
                        final int cx = findViewById(R.id.action_check).getWidth() / 2;
                        final int cy = findViewById(R.id.action_check).getHeight() / 2;
                        final int finalRadius = findViewById(R.id.action_check).getWidth();
                        Animator anim =
                                ViewAnimationUtils.createCircularReveal(findViewById(R.id.action_uncheck), cx, cy, 0, finalRadius);
                        check = true;
                        findViewById(R.id.action_uncheck).setVisibility(View.VISIBLE);
                        anim.setDuration(500);
                        anim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                findViewById(R.id.action_check).setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        anim.start();
                    }
                });

                findViewById(R.id.action_uncheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.action_check).setTranslationZ(1);
                        v.setTranslationZ(0);
                        final int cx = findViewById(R.id.action_uncheck).getWidth() / 2;
                        final int cy = findViewById(R.id.action_uncheck).getHeight() / 2;
                        final int finalRadius = findViewById(R.id.action_uncheck).getWidth();
                        Animator anim =
                                ViewAnimationUtils.createCircularReveal(findViewById(R.id.action_check), cx, cy, 0, finalRadius);
                        check = false;
                        findViewById(R.id.action_check).setVisibility(View.VISIBLE);
                        anim.setDuration(500);
                        anim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                findViewById(R.id.action_uncheck).setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        anim.start();
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(AddItem.this, getResources().getString(R.string.error) + " - " + intent.getAction(), Toast.LENGTH_SHORT).show();
                finishAfterTransition();
            }
        } else if (intent.getAction() != null && intent.getAction().equals("SUGGESTED")) {
            title.setText(intent.getExtras().getString("suggestion"));
        } else if (intent.getAction() != null && Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null) {
                title.setText(sharedText);
            }
        }

    }

    public void changeR(String content, boolean ok) {
        if (ok) {
            toShow.findViewById(R.id.selected_reminder).setBackgroundColor(getResources().getColor(R.color.icon_i));
            ((TextView) toShow.findViewById(R.id.selected_reminder_title)).setText(content);
        } else {
            toShow.findViewById(R.id.selected_reminder).setBackgroundColor(getResources().getColor(R.color.icon_r));
            ((TextView) toShow.findViewById(R.id.selected_reminder_title)).setText(content + "\n" + getString(R.string.warning_old_date));
        }

        toShow.setVisibility(View.VISIBLE);
    }

    public void changeItem(int position, final Dialog a, String info) {
        switch (position) {
            case 0:
                choose = "NOTIME";
                toShow.setVisibility(View.GONE);
                a.dismiss();
                break;
            case 1:
                choose = "TIME";
                DatePickerDialog mDatePicker = new DatePickerDialog(AddItem.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, final int selectedyear, final int selectedmonth, final int selectedday) {

                        TimePickerDialog mTimePicker = new TimePickerDialog(AddItem.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                year = selectedyear;
                                month = selectedmonth;
                                day = selectedday;
                                hour = selectedHour;
                                minute = selectedMinute;

                                // CONTROLLO DATA PASSATA
                                Calendar checkOld = Calendar.getInstance();
                                checkOld.set(Calendar.SECOND, 0);
                                Calendar reminderChoose = Calendar.getInstance();
                                reminderChoose.set(year, month, day, hour, minute);
                                if (checkOld.getTimeInMillis() > reminderChoose.getTimeInMillis()) {
                                    changeR(getResources().getString(R.string.snooze_until) + " " + getDate(), false);
                                } else {
                                    changeR(getResources().getString(R.string.snooze_until) + " " + getDate(), true);
                                }
                                changed = true;
                                a.dismiss();
                            }
                        }, hour, minute, true);
                        mTimePicker.show();

                    }
                }, year, month, day);
                mDatePicker.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                mDatePicker.setTitle("");
                mDatePicker.show();
                break;
            case 2:
                choose = "WHERE";
                changed = true;
                changeR(getResources().getString(R.string.snooze_until) + " " + place.getName(), true);
                a.dismiss();

                break;
            case 3:
                choose = "TIME";
                setTomorrow();
                changeR(getResources().getString(R.string.snooze_until) + " " + getDate(), true);
                changed = true;
                a.dismiss();
                break;
            case 4:
                choose = "BLUETOOTH";
                changeR(getResources().getString(R.string.snooze_until) + " " + info, true);
                changed = true;
                a.dismiss();
                break;
            case 5:
                choose = "WIFI";
                changeR(getResources().getString(R.string.snooze_until) + " " + info, true);
                changed = true;
                a.dismiss();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_item, menu);
        if(!edit) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveAll();
            finishAfterTransition();
            return true;
        }

        if (id == R.id.action_reminder) {
            final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reminder, null);

            final Dialog a = new Dialog(this);

            ((TextView) dialogView.findViewById(R.id.tomorrow_desc)).setText(getTomorrow());

            if (fixed)
                ((ImageView) dialogView.findViewById(R.id.pin)).setColorFilter(getResources().getColor(R.color.secondary_text));

            //TODO if (home != null) {
            if (false) {
                dialogView.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        place = home;
                        changeItem(2, a, "");
                    }
                });
            } else {
                dialogView.findViewById(R.id.home).setVisibility(View.GONE);
            }

            dialogView.findViewById(R.id.not).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeItem(0, a, "");
                }
            });
            dialogView.findViewById(R.id.time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeItem(1, a, "");
                }
            });
            dialogView.findViewById(R.id.place).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        AlertDialog.Builder choose_B = new AlertDialog.Builder(AddItem.this);
                        choose_B.setTitle(getResources().getString(R.string.action_choose_device));

                        List<Place> places = getPlaces();

                        if (places.size() > 0) {

                                    final String[] plDv = new String[places.size()];
                                    final String[] plDvID = new String[places.size()];
                                    int f = 0;
                                    for (Place placeA : places) {
                                                plDv[f] = placeA.getName();
                                                plDvID[f] = "" + placeA.getId();
                                                f++;
                                    }

                                    if (plDv.length == 1) {
                                        place = getPlace(plDvID[0]);
                                        changeItem(2, a, "");
                                    } else {

                                        choose_B.setSingleChoiceItems(plDv, 0, null)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.dismiss();
                                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                                        place = getPlace(plDvID[selectedPosition]);
                                                        changeItem(2, a, "");
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        choose_B.show();
                                    }
                                } else {
                                    Toast.makeText(AddItem.this, getResources().getString(R.string.no_places), Toast.LENGTH_SHORT).show();
                                }
                    }
            });
            dialogView.findViewById(R.id.tomorrow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeItem(3, a, "");
                }
            });
            dialogView.findViewById(R.id.bluetooth).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                        AlertDialog.Builder choose_B = new AlertDialog.Builder(AddItem.this);
                        choose_B.setTitle(getResources().getString(R.string.action_choose_device));
                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                        if (pairedDevices.size() > 0) {
                            SharedPreferences SP = getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                            if (!SP.contains("device_active")) {
                                SharedPreferences.Editor editor = SP.edit();
                                String toPut = "";
                                for (BluetoothDevice device : pairedDevices) {
                                    if (toPut.equals(""))
                                        toPut = device.getAddress();
                                    else
                                        toPut = toPut + "_" + device.getAddress();
                                }
                                editor.putString("device_active", toPut);
                                editor.apply();
                            }

                            try {
                                String[] deviceAct = SP.getString("device_active", "").split("_");
                                if (deviceAct.length > 0) {

                                    final String[] blDv = new String[deviceAct.length];
                                    final String[] blDvMAC = new String[deviceAct.length];
                                    int f = 0;
                                    for (BluetoothDevice device : pairedDevices) {

                                        for (int k = 0; k < deviceAct.length; k++) {
                                            if (device.getAddress().equals(deviceAct[k])) {
                                                blDv[f] = device.getName();
                                                blDvMAC[f] = device.getAddress();
                                                f++;
                                            }
                                        }
                                    }

                                    if (blDv.length == 1) {
                                        bluetoothAdd = blDvMAC[0];
                                        bluetoothName = blDv[0];
                                        changeItem(4, a, blDv[0]);
                                    } else {

                                        choose_B.setSingleChoiceItems(blDv, 0, null)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.dismiss();
                                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                                        bluetoothAdd = blDvMAC[selectedPosition];
                                                        bluetoothName = blDv[selectedPosition];
                                                        changeItem(4, a, blDv[selectedPosition]);
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        choose_B.show();
                                    }
                                } else {
                                    Toast.makeText(AddItem.this, getResources().getString(R.string.no_activated_device), Toast.LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException ex) {}
                        } else {
                            Toast.makeText(AddItem.this, getResources().getString(R.string.no_paired_devices), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddItem.this, getResources().getString(R.string.bluetooth_off), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            dialogView.findViewById(R.id.wifi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    WifiManager wifiM = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                    if (wifiM != null && wifiM.isWifiEnabled()) {
                        AlertDialog.Builder choose_W = new AlertDialog.Builder(AddItem.this);
                        choose_W.setTitle(getResources().getString(R.string.choose_wifi));
                        List<WifiConfiguration> wifiList = wifiM.getConfiguredNetworks();

                        if (wifiList.size() > 0) {
                            SharedPreferences SP = getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                            if (!SP.contains("wifi_active")) {
                                SharedPreferences.Editor editor = SP.edit();
                                String toPut = "";
                                for (WifiConfiguration connection : wifiList) {
                                    if (toPut.equals(""))
                                        toPut = "" + connection.networkId;
                                    else
                                        toPut = toPut + "_" + connection.networkId;
                                }
                                editor.putString("wifi_active", toPut);
                                editor.apply();
                            }

                            try {
                                String[] wifiAct = SP.getString("wifi_active", "").split("_");
                                if (wifiAct.length > 0) {
                                    final String[] wifiDv = new String[wifiAct.length];
                                    final String[] wifiMAC = new String[wifiAct.length];
                                    int f = 0;
                                    for (WifiConfiguration connection : wifiList) {

                                        for (int k = 0; k < wifiAct.length; k++) {
                                            if (("" + connection.networkId).equals(wifiAct[k])) {
                                                wifiDv[f] = connection.SSID.replace("\"", "");
                                                wifiMAC[f] = "" + connection.networkId;
                                                f++;
                                            }
                                        }
                                    }

                                    if (wifiDv.length == 1) {
                                        wifiAdd = wifiMAC[0];
                                        wifiName = wifiDv[0];
                                        changeItem(5, a, wifiDv[0]);
                                    } else {
                                        choose_W.setSingleChoiceItems(wifiDv, 0, null)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.dismiss();
                                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                                        wifiAdd = wifiMAC[selectedPosition];
                                                        wifiName = wifiDv[selectedPosition];
                                                        changeItem(5, a, wifiDv[selectedPosition]);
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        choose_W.show();
                                    }
                                } else  {
                                    Toast.makeText(AddItem.this, getResources().getString(R.string.no_activated_device), Toast.LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException ex) {}
                        } else {
                            Toast.makeText(AddItem.this, getResources().getString(R.string.no_wifi_saved), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddItem.this, getResources().getString(R.string.wifi_off), Toast.LENGTH_SHORT).show();
                    }

                }
            });


            dialogView.findViewById(R.id.pin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!fixed) {
                        fixed = true;
                        ((ImageView) v).setColorFilter(getResources().getColor(R.color.secondary_text));
                        Toast.makeText(AddItem.this, getResources().getString(R.string.item_pinned), Toast.LENGTH_SHORT).show();
                    } else {
                        fixed = false;
                        ((ImageView) v).setColorFilter(getResources().getColor(R.color.divider));
                        Toast.makeText(AddItem.this, getResources().getString(R.string.item_unpinned), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            a.setContentView(dialogView);
            a.setTitle(R.string.snooze_until);
            a.show();
        }

        if(id == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.ask_delete_reminder) + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ReminderService.startAction(AddItem.this, Costants.ACTION_DELETE, r);
                            finishAfterTransition();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!Utils.isEmpty(title) && ((r == null) || (r != null && !r.getTitle().equals(title.getText().toString())))) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.attention))
                    .setMessage(getResources().getString(R.string.ask_exit) + "?")
                    .setPositiveButton(R.string.action_exit_editor, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finishAfterTransition();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            finishAfterTransition();
        }
    }

    public void saveAll() {
        if (Utils.isEmpty(title)) {
            title.setError(getResources().getString(R.string.errore_title));
        } else {
            if (!edit) {
                generateReminder();
                ReminderService.startAction(AddItem.this, Costants.ACTION_CREATE, r);
            } else {
                getReminder();
                ReminderService.startAction(AddItem.this, Costants.ACTION_UPDATE, r);
            }
        }
    }

    public void getReminder() {
        Calendar c = Calendar.getInstance();
        String titleN = title.getText().toString();
        titleN = titleN.trim();

        switch (choose) {
            case "NOTIME":
                r.setTitle(titleN);
                r.setAlarm(Costants.ALARM_TYPE_NOTIME);
                if (!fixed)
                    r.setType("TODO");
                else
                    r.setType(Costants.ALARM_TYPE_FIXED);
                r.setDate_reminded(c.getTimeInMillis());
                if(check)
                    r.setDate_checked(c.getTimeInMillis());
                else
                    r.setDate_checked(0);
                break;
            case "TIME":
                r.setTitle(titleN);
                Calendar e = Calendar.getInstance();
                e.set(year,month,day,hour,minute);

                if (changed) {
                    r.setAlarm(Costants.ALARM_TYPE_TIME);
                    r.setAlarm_info("" + e.getTimeInMillis());
                    r.setType("REMINDER");
                    r.setDate_reminded(0);
                    r.setDate_checked(0);
                } else {
                    if (check) {
                        r.setDate_reminded(c.getTimeInMillis());
                        r.setDate_checked(c.getTimeInMillis());
                    } else {
                        r.setDate_checked(0);
                    }
                }
                break;
            case "WHERE":
                r.setTitle(titleN);
                if (changed) {
                    r.setAlarm(Costants.ALARM_TYPE_WHERE);
                    r.setAlarm_info("" + place.getId());
                    r.setType("PLACE");
                    r.setDate_reminded(0);
                    r.setDate_checked(0);
                } else {
                    if (check) {
                        r.setDate_reminded(c.getTimeInMillis());
                        r.setDate_checked(c.getTimeInMillis());
                    } else {
                        r.setDate_checked(0);
                    }
                }
                break;
            case "BLUETOOTH":
                r.setTitle(titleN);
                if (changed) {
                    r.setAlarm(Costants.ALARM_TYPE_BLUETOOTH);
                    r.setAlarm_info(bluetoothAdd + "_" + bluetoothName);
                    r.setType("DEVICE");
                    r.setDate_reminded(0);
                    r.setDate_checked(0);
                } else {
                    if (check) {
                        r.setDate_reminded(c.getTimeInMillis());
                        r.setDate_checked(c.getTimeInMillis());
                    } else {
                        r.setDate_checked(0);
                    }
                }
                break;
            case "WIFI":
                r.setTitle(titleN);
                if (changed) {
                    r.setAlarm(Costants.ALARM_TYPE_WIFI);
                    r.setAlarm_info(wifiAdd + "_" + wifiName);
                    r.setType("WIFI");
                    r.setDate_reminded(0);
                    r.setDate_checked(0);
                } else {
                    if (check) {
                        r.setDate_reminded(c.getTimeInMillis());
                        r.setDate_checked(c.getTimeInMillis());
                    } else {
                        r.setDate_checked(0);
                    }
                }
                break;
        }
    }

    public void generateReminder() {
        Calendar c = Calendar.getInstance();
        long dateC = c.getTimeInMillis();
        String titleN = title.getText().toString();
        titleN = titleN.trim();

        switch (choose) {
            case Costants.ALARM_TYPE_NOTIME:
                if (!fixed)
                    r = new Reminder(titleN, "", Costants.ALARM_TYPE_NOTIME, "", "TODO", dateC, dateC);
                else
                    r = new Reminder(titleN, "", Costants.ALARM_TYPE_NOTIME, "", Costants.ALARM_TYPE_FIXED, dateC, dateC);
                break;
            case Costants.ALARM_TYPE_TIME:
                c.set(year, month, day, hour, minute);
                r = new Reminder(titleN, "", Costants.ALARM_TYPE_TIME, "" + c.getTimeInMillis(), "REMINDER", dateC, 0);
                break;
            case Costants.ALARM_TYPE_WHERE:
                r = new Reminder(titleN, "", Costants.ALARM_TYPE_WHERE, "" + place.getId(), "PLACE", dateC, 0);
                break;
            case Costants.ALARM_TYPE_BLUETOOTH:
                r = new Reminder(titleN, "", Costants.ALARM_TYPE_BLUETOOTH, bluetoothAdd + "_" + bluetoothName, "DEVICE", dateC, 0);
                break;
            case Costants.ALARM_TYPE_WIFI:
                r = new Reminder(titleN, "", Costants.ALARM_TYPE_WIFI, wifiAdd + "_" + wifiName, "WIFI", dateC, 0);
                break;
        }
    }

    public String getDate() {
        Calendar c = Calendar.getInstance();
        c.set(year,month,day,hour,minute);
        Date data = new Date(c.getTimeInMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM d, HH:mm");
        return dateFormat.format(data).toString();
    }

    public void resetCal() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        if (hour < 9) {
            hour = 9;
            minute = 0;
        } else if (hour >= 9 && hour < 12) {
            hour = 12;
            minute = 0;
        } else if (hour >=  12 && hour < 19) {
            hour = 19;
            minute = 0;
        } else if (hour >= 19  && hour < 22) {
            hour = 22;
            minute = 0;
        } else if (hour >= 22) {
            hour = 9;
            minute = 0;
            day = 1;
            if (month == 11 && day == 31) {
                month = 0;
                year++;
            } else if (day == 31 && (month == 0 || month == 2 || month == 4 || month == 6 || month == 7 || month == 9)) {
                month++;
            } else if (day == 30 && (month == 3 || month == 5 || month == 8 || month == 10)) {
                month++;
            } else if (day == 29 && month == 1) {
                month = 2;
            } else if (day == 28 && month == 1) {
                month = 2;
            } else {
                day++;
            }
        }
    }

    public String getTomorrow() {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.MONTH) == 11 && c.get(Calendar.DAY_OF_MONTH) == 31) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 31 && (c.get(Calendar.MONTH) == 0 || c.get(Calendar.MONTH) == 2 || c.get(Calendar.MONTH) == 4 || c.get(Calendar.MONTH) == 6 || c.get(Calendar.MONTH) == 7 || c.get(Calendar.MONTH) == 9)) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 30 && (c.get(Calendar.MONTH) == 3 || c.get(Calendar.MONTH) == 5 || c.get(Calendar.MONTH) == 8 || c.get(Calendar.MONTH) == 10)) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 29 && c.get(Calendar.MONTH) == 1) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, 2);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 28 && c.get(Calendar.MONTH) == 1) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, 2);
        } else {
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        }
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        Date data = new Date(c.getTimeInMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM d, HH:mm");
        return dateFormat.format(data).toString();
    }

    public void setTomorrow() {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.MONTH) == 11 && c.get(Calendar.DAY_OF_MONTH) == 31) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 31 && (c.get(Calendar.MONTH) == 0 || c.get(Calendar.MONTH) == 2 || c.get(Calendar.MONTH) == 4 || c.get(Calendar.MONTH) == 6 || c.get(Calendar.MONTH) == 7 || c.get(Calendar.MONTH) == 9)) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 30 && (c.get(Calendar.MONTH) == 3 || c.get(Calendar.MONTH) == 5 || c.get(Calendar.MONTH) == 8 || c.get(Calendar.MONTH) == 10)) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 29 && c.get(Calendar.MONTH) == 1) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, 2);
        } else if (c.get(Calendar.DAY_OF_MONTH) == 28 && c.get(Calendar.MONTH) == 1) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, 2);
        } else {
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        }
        hour = 9;
        minute = 0;
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public Place getPlace(String id) {
        DbAdapterP dbHelperP = new DbAdapterP(AddItem.this);
        dbHelperP.open();
        Place p = null;
        Cursor c = dbHelperP.getPlaceById(id);
        if (c.moveToNext())
            p = new Place(c);
        c.close();
        dbHelperP.close();
        return p;
    }

    public List<Place> getPlaces() {
        DbAdapterP dbHelperP = new DbAdapterP(AddItem.this);
        dbHelperP.open();
        List<Place> pL = new ArrayList<>();
        Cursor c = dbHelperP.fetchAllPlaces();
        while (c.moveToNext())
            pL.add(new Place(c));
        c.close();
        dbHelperP.close();
        return pL;
    }

}
