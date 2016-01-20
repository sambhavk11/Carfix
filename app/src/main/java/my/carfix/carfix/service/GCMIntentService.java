package my.carfix.carfix.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.util.Log;

import my.carfix.carfix.R;
import my.carfix.carfix.activity.MainDrawerActivity;
import my.carfix.carfix.activity.PolicyExpireActivity;
import my.carfix.carfix.activity.SplashScreenActivity;

import my.carfix.carfix.activity.TaskAssignedActivity;
import my.carfix.carfix.activity.TaskCanceledActivity;
import my.carfix.carfix.content.CarfixSQLiteOpenHelper;
import my.carfix.carfix.model.LogCase;
import my.carfix.carfix.model.LogCaseMessage;
import my.carfix.carfix.model.TruckDestination;

public class GCMIntentService extends IntentService
{
    public static final int NOTIFICATION_ID = 1;

    private CarfixSQLiteOpenHelper dbHelper;

    public static final String TAG = "GCM Demo";

    public GCMIntentService()
    {
        super("GCMIntentService");

        dbHelper = new CarfixSQLiteOpenHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (! extras.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
            {
                //sendNotification("Send error: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
            {
                //sendNotification("Deleted messages on server: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                if (extras.getString("type") == null)
                {
                    LogCase logCase = new LogCase();

                    logCase.setCaseNo(extras.getString("caseNo"));
                    logCase.setTruckNo(extras.getString("truckNo"));
                    logCase.setArrivedTime(Long.parseLong(extras.getString("arrivedTime")));
                    logCase.setLocation(extras.getString("location"));
                    logCase.setDriverName(extras.getString("driverName"));
                    logCase.setImageURL(extras.getString("imageURL"));

                    if (fetchLogCase(logCase.getCaseNo()) == null) {
                        insertLogCase(logCase);
                    } else {
                        updateLogCase(logCase);
                    }

                    LogCaseMessage logCaseMessage = new LogCaseMessage();

                    logCaseMessage.setCaseNo(extras.getString("caseNo"));
                    logCaseMessage.setMessage(extras.getString("message"));
                    logCaseMessage.setMessageTime(Long.parseLong(extras.getString("messageTime")));

                    insertLogCaseMessage(logCaseMessage);

                    sendCaseUpdateNotification(extras, String.format("Case #%s : %s", extras.getString("caseNo"), extras.getString("message")));
                }
                else if (extras.getString("type").equals("taskAssigned"))
                {
                    sendTaskAssignedNotification(extras, String.format("To #%s : Case #%s Assigned", extras.getString("truckNo"), extras.getString("caseNo")));
                }
                else if (extras.getString("type").equals("jobCancel"))
                {
                    dbHelper.getWritableDatabase().delete(TruckDestination.TABLE_NAME, null, null);

                    sendTaskCanceledNotification(extras, String.format("To #%s : Case #%s Canceled", extras.getString("truckNo"), extras.getString("caseNo")));
                }
                else if (extras.getString("type").equals("policyNotification"))
                {
                    sendPolicyExpireNotification(extras, String.format("Policy for %s will expire soon", extras.getString("vehRegNo")));
                }

                Log.i(TAG, "Received: " + extras.toString());
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private LogCase fetchLogCase(String caseNo)
    {
        Cursor cursor = dbHelper.getReadableDatabase().query(LogCase.TABLE_NAME, null, "caseNo = ?", new String[]{caseNo}, null, null, null);

        if (cursor.getCount() == 1)
        {
            cursor.moveToNext();

            LogCase logCase = new LogCase();
            logCase.loadCursor(cursor);

            return logCase;
        }

        cursor.close();

        return null;
    }

    private void insertLogCase(LogCase logCase)
    {
        dbHelper.getWritableDatabase().insert(LogCase.TABLE_NAME, null, logCase.toContentValues());
    }

    private void updateLogCase(LogCase logCase)
    {
        dbHelper.getWritableDatabase().update(LogCase.TABLE_NAME, logCase.toContentValues(), "caseNo = ?", new String[]{logCase.getCaseNo()});
    }

    private void insertLogCaseMessage(LogCaseMessage logCaseMessage)
    {
        dbHelper.getWritableDatabase().insert(LogCaseMessage.TABLE_NAME, null, logCaseMessage.toContentValues());
    }

    private void sendCaseUpdateNotification(Bundle extras, String msg)
    {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(this, MainDrawerActivity.class);

        activityIntent.putExtra("caseNo", extras.getString("caseNo"));

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis() % Integer.MAX_VALUE), activityIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher2)
                        .setContentTitle("Carfix - Case Updated")
                        .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify((int)(System.currentTimeMillis() % Integer.MAX_VALUE), mBuilder.build());
    }

    private void sendPolicyExpireNotification(Bundle extras, String msg)
    {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(this, PolicyExpireActivity.class);

        activityIntent.putExtra("vehRegNo", extras.getString("vehRegNo"));
        activityIntent.putExtra("policyEffective", Long.parseLong(extras.getString("policyEffective")));
        activityIntent.putExtra("policyExpired", Long.parseLong(extras.getString("policyExpired")));
        activityIntent.putExtra("message", extras.getString("message"));

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis() % Integer.MAX_VALUE),/*Integer.parseInt(extras.getString("caseNo"))*/activityIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher2)
                .setContentTitle("Carfix - Policy Reminder")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify((int)(System.currentTimeMillis() % Integer.MAX_VALUE), mBuilder.build());
    }
    private void sendTaskCanceledNotification(Bundle extras, String msg)
    {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(this, TaskCanceledActivity.class);

        activityIntent.putExtra("userCode", extras.getString("userCode"));
        activityIntent.putExtra("truckNo", extras.getString("truckNo"));
        activityIntent.putExtra("key", extras.getString("key"));
        activityIntent.putExtra("caseNo", extras.getString("caseNo"));
        activityIntent.putExtra("vehRegNo", extras.getString("vehRegNo"));

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis() % Integer.MAX_VALUE),/*Integer.parseInt(extras.getString("caseNo"))*/activityIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher2)
                .setContentTitle("Carfix - Task Canceled")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify((int)(System.currentTimeMillis() % Integer.MAX_VALUE), mBuilder.build());
    }

    private void sendTaskAssignedNotification(Bundle extras, String msg)
    {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(this, TaskAssignedActivity.class);

        activityIntent.putExtra("userCode", extras.getString("userCode"));
        activityIntent.putExtra("truckNo", extras.getString("truckNo"));
        activityIntent.putExtra("key", extras.getString("key"));
        activityIntent.putExtra("caseNo", extras.getString("caseNo"));
        activityIntent.putExtra("vehRegNo", extras.getString("vehRegNo"));
        activityIntent.putExtra("vehModel", extras.getString("vehModel"));
        activityIntent.putExtra("phoneNo", extras.getString("phoneNo"));
        activityIntent.putExtra("service", Integer.parseInt(extras.getString("service")));
        activityIntent.putExtra("breakdownAddress", extras.getString("breakdownAddress"));
        activityIntent.putExtra("breakdownLatitude", Double.parseDouble(extras.getString("breakdownLatitude")));
        activityIntent.putExtra("breakdownLongitude", Double.parseDouble(extras.getString("breakdownLongitude")));
        activityIntent.putExtra("destinationAddress", extras.getString("destinationAddress"));
        activityIntent.putExtra("destinationLatitude", Double.parseDouble(extras.getString("destinationLatitude")));
        activityIntent.putExtra("destinationLongitude", Double.parseDouble(extras.getString("destinationLongitude")));

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis() % Integer.MAX_VALUE),/*Integer.parseInt(extras.getString("caseNo"))*/activityIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher2)
                .setContentTitle("Carfix - Task Assigned")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify((int)(System.currentTimeMillis() % Integer.MAX_VALUE), mBuilder.build());
    }
}
