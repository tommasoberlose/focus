package com.nego.focus;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nego.reminders.Functions.LocationF;
import nego.reminders.Functions.NotificationF;
import nego.reminders.Functions.ReminderService;
import nego.reminders.database.DbAdapter;


public class Utils {

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public static void oldReminder(Context context) {

        DbAdapter dbHelper = new DbAdapter(context);
        dbHelper.open();

        // Controllo di eventuali promemoria vecchi
        Cursor c = dbHelper.fetchAllReminders();
        Calendar now = Calendar.getInstance();
        while (c.moveToNext()) {
            Reminder r = new Reminder(c);
            if (r.getAlarm().equals(Costants.ALARM_TYPE_TIME) && Long.parseLong(r.getAlarm_info()) <= now.getTimeInMillis() ) {
                ReminderService.startAction(context, Costants.ACTION_REMINDED, r);
                NotificationF.Notification(context, r);
            }
        }
        c.close();

        // Controllo di eventuali promemoria fissi
        c = dbHelper.fetchAllReminders();
        while (c.moveToNext()) {
            Reminder r = new Reminder(c);
            if (r.getAlarm().equals(Costants.ALARM_TYPE_NOTIME) && r.getType().equals("FIXED")) {
                //NotificationF.NotificationFixed(context, r);
            }
        }
        c.close();
        dbHelper.close();
    }



    // SNACKBAR

    public static void SnackbarC(final Context context, String title, final Reminder r, String action, final View view) {
        if (Costants.ACTION_UNCHECKED.equals(action)) {
            Snackbar.make(view, title, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReminderService.startAction(context, Costants.ACTION_UNCHECKED, r);
                        }
                    })
                    .setActionTextColor(context.getResources().getColor(android.R.color.white))
                    .show();
        } else if (Costants.ACTION_UNDELETE.equals(action)) {
            Snackbar.make(view, title, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReminderService.startAction(context, Costants.ACTION_UNDELETE, r);
                        }
                    })
                    .setActionTextColor(context.getResources().getColor(android.R.color.white))
                    .show();
        }

    }

    public static void SnackbarC(final Context context, String title, final ArrayList<Reminder> r, String action, final View view) {
        if (Costants.ACTION_UNDELETE_MULTI.equals(action)) {
            Snackbar.make(view, title, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReminderService.startAction(context, Costants.ACTION_UNDELETE_MULTI, r);
                        }
                    })
                    .setActionTextColor(context.getResources().getColor(android.R.color.white))
                    .show();
        }

    }

    public static void SnackbarC(final Context context, String title, final View view) {

        Snackbar.make(view, title, Snackbar.LENGTH_LONG)
                .setActionTextColor(context.getResources().getColor(android.R.color.white))
                .show();

    }


    // Suggerimenti Home

    public static ArrayList<String> suggestionRandom(Context context, int k) {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> listRandom = new ArrayList<String>();

        list.add(context.getResources().getString(R.string.suggestion_1));
        list.add(context.getResources().getString(R.string.suggestion_2));
        list.add(context.getResources().getString(R.string.suggestion_3));
        list.add(context.getResources().getString(R.string.suggestion_4));
        list.add(context.getResources().getString(R.string.suggestion_5));
        list.add(context.getResources().getString(R.string.suggestion_6));
        list.add(context.getResources().getString(R.string.suggestion_7));
        list.add(context.getResources().getString(R.string.suggestion_8));
        list.add(context.getResources().getString(R.string.suggestion_9));
        list.add(context.getResources().getString(R.string.suggestion_10));
        list.add(context.getResources().getString(R.string.suggestion_11));
        list.add(context.getResources().getString(R.string.suggestion_12));
        list.add(context.getResources().getString(R.string.suggestion_13));
        list.add(context.getResources().getString(R.string.suggestion_14));
        list.add(context.getResources().getString(R.string.suggestion_15));

        Random r = new Random();
        int MAX = list.size();
        for (int i = 0;i<k;i++) {
            int random = r.nextInt(MAX -i);
            listRandom.add(list.get(random));
            list.remove(random);
        }
        return listRandom;
    }

    public static int itemsToDo(Context context) {
        DbAdapter dbHelper = new DbAdapter(context);
        dbHelper.open();
        int count = dbHelper.fetchRemindersByFilterTodo().getCount();
        dbHelper.close();
        return count;
    }

    public static void notification_add_update(Context context) {
        SharedPreferences SP = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        if (SP.contains("enable_not_add") && SP.getBoolean("enable_not_add", false))
            NotificationF.NotificationAdd(context);
    }

    public static void update_receiver(Context context) {
        //AlarmF.updateAlarm(context);
        LocationF.LocationUpdate(context);
    }

    public static String getDate(long date) {
        Calendar today = Calendar.getInstance();
        Calendar byR = Calendar.getInstance();
        byR.setTimeInMillis(date);
        SimpleDateFormat HM = new SimpleDateFormat("HH:mm");
        SimpleDateFormat DM = new SimpleDateFormat("MMM d, HH:mm");
        SimpleDateFormat MY = new SimpleDateFormat("MMM d y, HH:mm ");
        if (today.get(Calendar.YEAR) == byR.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == byR.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == byR.get(Calendar.DAY_OF_MONTH)) {
            return HM.format(new Date(byR.getTimeInMillis())).toString();
        } else if (today.get(Calendar.YEAR) == byR.get(Calendar.YEAR)) {
            return DM.format(new Date(byR.getTimeInMillis())).toString();
        } else {
            return MY.format(new Date(byR.getTimeInMillis())).toString();
        }
    }

    public static String getHour(long date) {
        Calendar byR = Calendar.getInstance();
        byR.setTimeInMillis(date);
        SimpleDateFormat HM = new SimpleDateFormat("HH:mm");
        return HM.format(new Date(byR.getTimeInMillis())).toString();
    }

    public static void tab_intro(Context context) {
        /*SharedPreferences SP = context.getSharedPreferences(Costants.PREFERENCES_COSTANT, Context.MODE_PRIVATE);
        if (!SP.contains("first_use") || !SP.getBoolean("first_use", true)) {
            SharedPreferences.Editor editor = SP.edit();
            editor.putBoolean("first_use", true);
            editor.apply();
            context.startActivity(new Intent(context, Intro.class));
        } TODO*/
    }


    public static Address getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        int i = 0;
        while (i<100) {
            try {
                address = coder.getFromLocationName(strAddress, 5);
                if (address != null) {
                    return address.get(0);
                }

            } catch (Exception ex) {
            }
            i++;
        }

        return  null;

    }


    public static Address getAddressFromLocation(Context context, String lat, String lon) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        int i = 0;
        while (i<100) {
            try {
                address = coder.getFromLocation(new Double(lat), new Double(lon), 5);
                if (address != null) {
                    return address.get(0);
                }

            } catch (Exception ex) {
            }
            i++;
        }

        return  null;

    }

}
