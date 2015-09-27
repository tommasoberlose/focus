package nego.reminders.Functions;

import android.content.Context;
import android.database.Cursor;

import nego.reminders.LocationL;
import nego.reminders.Place;
import nego.reminders.database.DbAdapter;
import nego.reminders.database.DbAdapterP;

public class LocationF {
    public static void LocationUpdate(Context context) {
/*
        DbAdapter dbHelper = new DbAdapter(context);
        dbHelper.open();

        int count = dbHelper.fetchRemindersByFilterPlaces().getCount();
        if (count > 0)
            LocationL.startLocationListener(context);
        else
            LocationL.stopLocationListener(context);

        dbHelper.close();*/
    }

    public static Place getPlace(Context context, String id) {
        DbAdapterP dbHelperP = new DbAdapterP(context);
        dbHelperP.open();
        Place p = null;
        Cursor c = dbHelperP.getPlaceById(id);
        if (c.moveToNext())
            p = new Place(c);
        dbHelperP.close();
        return p;
    }
}
