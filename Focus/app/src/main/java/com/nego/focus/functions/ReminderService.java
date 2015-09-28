package com.nego.focus.functions;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;

import nego.reminders.Costants;
import nego.reminders.Reminder;
import nego.reminders.Utils;
import nego.reminders.database.DbAdapter;

public class ReminderService extends IntentService {

    public static void startAction(Context context, String action, Reminder r) {
        Intent intent = new Intent(context, ReminderService.class);
        intent.setAction(action);
        intent.putExtra(Costants.EXTRA_REMINDER, r);
        context.startService(intent);
    }

    public static void startAction(Context context, String action, ArrayList<Reminder> RM) {
        Intent intent = new Intent(context, ReminderService.class);
        intent.setAction(action);
        intent.putParcelableArrayListExtra(Costants.EXTRA_REMINDER, RM);
        context.startService(intent);
    }

    private void sendResponse(String s, Reminder r) {
        Utils.notification_add_update(this);
        Intent i = new Intent(Costants.ACTION_UPDATE_LIST);
        i.putExtra(Costants.EXTRA_ACTION_TYPE, s);
        i.putExtra(Costants.EXTRA_REMINDER, r);
        sendBroadcast(i);
    }

    private void sendResponse(String s, ArrayList<Reminder> RM) {
        Utils.notification_add_update(this);
        Intent i = new Intent(Costants.ACTION_UPDATE_LIST);
        i.putExtra(Costants.EXTRA_ACTION_TYPE, s);
        i.putParcelableArrayListExtra(Costants.EXTRA_REMINDER, RM);
        sendBroadcast(i);
    }

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Costants.ACTION_CREATE.equals(action)) {
                final Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                createReminder(r);
            } else if (Costants.ACTION_UPDATE.equals(action)) {
                final Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                updateReminder(r);
            } else if (Costants.ACTION_DELETE.equals(action)) {
                final Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                deleteReminder(r);
            } else if (Costants.ACTION_UNDELETE.equals(action)) {
                final Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                undeleteReminder(r);
            } else if (Costants.ACTION_DELETE_MULTI.equals(action)) {
                final ArrayList<Reminder> r = intent.getParcelableArrayListExtra(Costants.EXTRA_REMINDER);
                deleteReminder(r);
            } else if (Costants.ACTION_UNDELETE_MULTI.equals(action)) {
                final ArrayList<Reminder> r = intent.getParcelableArrayListExtra(Costants.EXTRA_REMINDER);
                undeleteReminder(r);
            } else if (Costants.ACTION_REMINDED.equals(action)) {
                final Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                closeReminder(r);
            } else if (Costants.ACTION_CHECKED.equals(action)) {
                final Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                checkReminder(r);
            } else if (Costants.ACTION_UNCHECKED.equals(action)) {
                final Reminder r = intent.getParcelableExtra(Costants.EXTRA_REMINDER);
                uncheckReminder(r);
            }
        }
    }

    private void createReminder(Reminder r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.create_reminder(this, dbHelper)) {
            sendResponse(Costants.ACTION_CREATE, r);
        }
        dbHelper.close();
    }

    private void updateReminder(Reminder r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.update_reminder(this, dbHelper)) {
            sendResponse(Costants.ACTION_UPDATE, r);
        }
        dbHelper.close();
    }

    private void deleteReminder(Reminder r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (ReminderF.delete_reminder(this, r)) {
            sendResponse(Costants.ACTION_DELETE, r);
        }
        dbHelper.close();
    }

    private void deleteReminder(ArrayList<Reminder> RM) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        int count = 0;
        for (Reminder r : RM) {
            if (ReminderF.delete_reminder(this, r))
                count++;
        }
        if (count == RM.size())
            sendResponse(Costants.ACTION_DELETE_MULTI, RM);
        dbHelper.close();
    }

    private void undeleteReminder(Reminder r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (ReminderF.undo_delete_reminder(this, r)) {
            sendResponse(Costants.ACTION_UNDELETE, r);
        }
        dbHelper.close();
    }

    private void undeleteReminder(ArrayList<Reminder> RM) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        int count = 0;
        for (Reminder r : RM) {
            if (ReminderF.undo_delete_reminder(this, r))
                count++;
        }
        if (count == RM.size())
            sendResponse(Costants.ACTION_UNDELETE_MULTI, RM);
        dbHelper.close();
    }

    private void closeReminder(Reminder r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        Calendar now = Calendar.getInstance();
        r.setDate_reminded(now.getTimeInMillis());
        if (r.update_reminder(this, dbHelper)) {
            sendResponse(Costants.ACTION_REMINDED, r);
        }
        dbHelper.close();
    }

    private void checkReminder(Reminder r) {
        if (ReminderF.check_reminder(this, r)) {
            sendResponse(Costants.ACTION_CHECKED, r);
        }
    }

    private void uncheckReminder(Reminder r) {
        if (ReminderF.uncheck_reminder(this, r)) {
            sendResponse(Costants.ACTION_UNCHECKED, r);
        }
    }
}
