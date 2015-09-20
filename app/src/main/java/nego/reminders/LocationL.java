package nego.reminders;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.location.*;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import nego.reminders.Functions.NotificationF;
import nego.reminders.database.DbAdapter;
import nego.reminders.database.DbAdapterP;

public class LocationL extends Service {

    private static final String ACTION_LOCATION_ON = "intent.action.LOCATION_ON";
    private static final String ACTION_LOCATION_OFF = "intent.action.LOCATION_OFF";
    private static final int TWO_MINUTES = 1000 * 60 * 30;
    public MyLocationListener listener;
    public LocationManager locationManager;


    public static void startLocationListener(Context context) {
        Intent intent = new Intent(context, LocationL.class);
        intent.setAction(ACTION_LOCATION_ON);
        context.startService(intent);
    }

    public static void stopLocationListener(Context context) {
        Intent intent = new Intent(context, LocationL.class);
        intent.setAction(ACTION_LOCATION_OFF);
        context.startService(intent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOCATION_ON.equals(action)) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                listener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TWO_MINUTES, 200, listener);
            } else if (ACTION_LOCATION_OFF.equals(action)) {
                if (locationManager != null)
                    locationManager.removeUpdates(listener);
            }
        }
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(Location location) {
            DbAdapterP dbHelper = new DbAdapterP(LocationL.this);
            dbHelper.open();
            DbAdapter dbHelper1 = new DbAdapter(LocationL.this);
            dbHelper1.open();

            Cursor c = dbHelper1.fetchAllReminders();
            while (c.moveToNext()) {
                Reminder r = new Reminder(c);
                if (r.getAlarm().equals(Costants.ALARM_TYPE_WHERE)) {
                    Place p = getPlace(dbHelper, r.getAlarm_info());
                    Location l = new Location("");
                    l.setLatitude(new Double(p.getLatitude()));
                    l.setLongitude(new Double(p.getLongitude()));
                    if (location.distanceTo(l) < 100) {
                        Calendar cal = Calendar.getInstance();
                        r.setDate_reminded(cal.getTimeInMillis());
                        r.update_reminder(dbHelper1);
                        NotificationF.Notification(LocationL.this, r);
                    }
                }
            }
            c.close();

            dbHelper1.close();
            dbHelper.close();
        }


        public void onProviderDisabled(String provider)
        {
        }


        public void onProviderEnabled(String provider)
        {
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        public Place getPlace(DbAdapterP dbHelperP, String id) {
            dbHelperP.open();
            Place p = null;
            Cursor c = dbHelperP.getPlaceById(id);
            if (c.moveToNext())
                p = new Place(c);
            c.close();
            return p;
        }


        public Address getAddressFromLocation(String lat, String lon) {

            Geocoder coder = new Geocoder(LocationL.this);
            List<Address> address;

            try {
                address = coder.getFromLocation(new Double(lat), new Double(lon), 5);
                if (address != null) {
                    return address.get(0);
                }

            } catch (Exception ex) {}

            return  null;

        }

    }
}
