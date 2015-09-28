package com.nego.focus.receiver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import nego.reminders.Costants;
import nego.reminders.Main;
import nego.reminders.R;

public class ShortcutReceiver extends Activity {

    public void onCreate(Bundle savedInstanceState) {

        if (getIntent().getAction().equals("android.intent.action.CREATE_SHORTCUT")) {
            Intent shortcutIntent = new Intent(getApplicationContext(), Main.class);
            shortcutIntent.setAction(Costants.EXTRA_ACTION_ADD);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.title_activity_add_item));
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.mipmap.shortcut_add));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

            setResult(RESULT_OK, addIntent);
            finish();
        }

        super.onCreate(savedInstanceState);
    }
}
