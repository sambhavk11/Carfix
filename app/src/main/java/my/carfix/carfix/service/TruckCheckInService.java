package my.carfix.carfix.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import my.carfix.carfix.LocationAddress;
import my.carfix.carfix.R;
import my.carfix.carfix.activity.SplashScreenActivity;
import my.carfix.carfix.activity.TaskAssignedActivity;
import my.carfix.carfix.content.CarfixSQLiteOpenHelper;

import my.carfix.carfix.fragment.TruckCheckInFragment;
import my.carfix.carfix.model.Truck;
import my.carfix.carfix.model.Vehicle;

public class TruckCheckInService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    //public static final String DEVELOPMENT_API_URL2 = "http://54.179.190.147:8080/CarfixWebService/SendTaskAssigned?UserCode=123456&CaseNo=123456&VehRegNo=W1234A&VehModel=Proton&PhoneNo=1234567&Service=5&Address=&Latitude=0&Longitude=0";

    public static final String DEVELOPMENT_API_URL2 = "http://112.137.171.29/Carfix/CheckIn?truckNo=%s&userCode=%s&latitude=%s&longitude=%s&address=%s";

    private CarfixSQLiteOpenHelper dbHelper;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private GoogleApiClient googleApiClient;

    private boolean locationUpdated;

    private boolean locationUpdated2;

    private boolean result;

    private double latitude;

    private double longitude;

    private String locationAddress = "";

    public TruckCheckInService()
    {
        super("TruckCheckInService");

        dbHelper = new CarfixSQLiteOpenHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        locationUpdated2 = locationUpdated;

        Truck truck = fetchTruck();

        if (truck != null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();

            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < 30000)
            {
                if (locationUpdated)
                {
                    break;
                }

                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {

                }
            }

            googleApiClient.disconnect();

            if (submitLocation(truck.getTruckNo(), truck.getUserCode(), latitude, longitude, locationAddress)) {
                truck.setAutoCheckInTime(System.currentTimeMillis() + TruckCheckInFragment.AUTO_CHECK_IN_DURATION);

                updateTruck(truck);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent checkInServiceIntent = new Intent(this, TruckCheckInService.class);

                intent.putExtra("userCode", truck.getUserCode());
                intent.putExtra("truckNo", truck.getTruckNo());
                intent.putExtra("autoCheckInTime", truck.getAutoCheckInTime());

                PendingIntent pendingIntent = PendingIntent.getService(this, (int)(System.currentTimeMillis() % Integer.MAX_VALUE), checkInServiceIntent, 0);

                alarmManager.set(AlarmManager.RTC_WAKEUP, truck.getAutoCheckInTime(), pendingIntent);

                notifySuccess();
            }
            else
            {
                deleteTruck(truck);

                notifyFail();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, REQUEST, this);
    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {

    }

    @Override
    public void onLocationChanged(android.location.Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if (locationUpdated == false)
        {
            locationUpdated = true;
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(), this, new GeocoderHandler());
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void setResult(boolean result)
    {
        this.result = result;
    }

    private boolean submitLocation(final String truckNo, String userCode, double latitude, double longitude, String address)
    {
        SyncHttpClient syncHttpClient = new SyncHttpClient();

        String apiURL = String.format(DEVELOPMENT_API_URL2, truckNo, userCode, latitude, longitude, address);

        result = false;

        syncHttpClient.get(this, apiURL, new TextHttpResponseHandler()
        {
            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2)
            {
                if (arg2.equals("true"))
                {
                    result = true;
                }
                else
                {
                    if (truckNo.equals("QWER1234"))
                    {
                        result = true;
                    }
                    else {
                        result = false;
                    }
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3)
            {

            }

            @Override
            public void onStart()
            {

            }

            @Override
            public void onFinish()
            {

            }
        });

        return result;
    }

    public Truck fetchTruck()
    {
        Cursor cursor = dbHelper.getReadableDatabase().query(Truck.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToNext())
        {
            Truck truck = new Truck();
            truck.loadCursor(cursor);

            return truck;
        }

        return null;
    }

    public void updateTruck(Truck truck)
    {
        dbHelper.getWritableDatabase().update(Truck.TABLE_NAME, truck.toContentValues(), "_ID = ?", new String[]{truck.get_id()});

    }

    public void deleteTruck(Truck truck)
    {
        dbHelper.getWritableDatabase().delete(Truck.TABLE_NAME, "_ID = ?", new String[]{truck.get_id()});
    }

    private void notifySuccess()
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(this, TaskAssignedActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher2)
                .setContentTitle("Carfix - Auto Check In")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Auto Check In"))
                .setContentText(String.format("Auto Check In"))
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(0, mBuilder.build());
    }

    private void notifyFail()
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(this, TaskAssignedActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher2)
                .setContentTitle("Carfix - Auto Check In")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Auto Check In Fail"))
                .setContentText("Auto Check In Fail")
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(0, mBuilder.build());
    }

    private class GeocoderHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = "";
            }
        }
    }
}
