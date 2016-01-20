package my.carfix.carfix.activity;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import my.carfix.carfix.R;
import my.carfix.carfix.content.CarfixSQLiteOpenHelper;

public class SplashScreenActivity extends ActionBarActivity
{
    public static String deviceID = "";

    private static final String PROPERTY_REG_ID = "registration_id";

    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String SENDER_ID = "1027199507348";

    private static final String TAG = "GCMWebService";

    private CarfixSQLiteOpenHelper dbHelper;

    private GoogleCloudMessaging gcm;

    public SplashScreenActivity()
    {
        dbHelper = new CarfixSQLiteOpenHelper(this);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash_screen);

        if (checkPlayServices())
        {
            gcm = GoogleCloudMessaging.getInstance(this);
            deviceID = getRegistrationId(getApplicationContext());

            if (deviceID.isEmpty())
            {
                registerInBackground();
            }
            else
            {
                Handler handler = new Handler();

                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from guide", null);

                        if (cursor.moveToNext())
                        {
                            gotoMainDrawerActivity();
                        }
                        else
                        {
                            gotoGuideActivity();
                        }
                    }
                }, 2000);
            }
        }
        else
        {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
	}

    @Override
    protected void onResume()
    {
        super.onResume();

        checkPlayServices();
    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(TAG, "This device is not supported.");
                finish();
            }

            return false;
        }

        return true;
    }

    private SharedPreferences getGcmPreferences(Context context)
    {
        return getSharedPreferences(SplashScreenActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch (NameNotFoundException e)
        {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGcmPreferences(context);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty())
        {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion)
        {
            Log.i(TAG, "App version changed.");
            return "";
        }

        return registrationId;
    }

    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGcmPreferences(context);

        int appVersion = getAppVersion(context);

        Log.i(TAG, "Saving regId on app version " + appVersion);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);

        editor.commit();
    }

    private void registerInBackground()
    {
        final Context context = this;

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    deviceID = gcm.register(SENDER_ID);

                    storeRegistrationId(getApplicationContext(), deviceID);
                }
                catch (IOException ex)
                {
                    return null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void msg)
            {
                Handler handler = new Handler();

                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from guide", null);

                        if (cursor.moveToNext())
                        {
                            gotoMainDrawerActivity();
                        }
                        else
                        {
                            gotoGuideActivity();
                        }
                    }
                }, 2000);
            }
        }.execute(null, null, null);
    }

    private void gotoMainDrawerActivity()
    {
        Intent mIntent = new Intent(this, MainDrawerActivity.class);

        startActivity(mIntent);

        this.finish();
    }

    private void gotoGuideActivity()
    {
        Intent mIntent = new Intent(this, GuideActivity.class);

        startActivity(mIntent);

        this.finish();
    }
}
