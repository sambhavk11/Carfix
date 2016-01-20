package my.carfix.carfix.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import my.carfix.carfix.R;
import my.carfix.carfix.content.CarfixSQLiteOpenHelper;
import my.carfix.carfix.model.Truck;
import my.carfix.carfix.model.TruckDestination;
import my.carfix.datastructure.WorkshopsData;

public class TaskAssignedActivity extends ActionBarActivity
{
    public static final String TRUCK_ACCEPT_API = "http://112.137.171.29/Carfix/Case/TruckAccept?key=%s&truckNo=%s&userCode=%s";

    public static final String TRUCK_REJECT_API = "http://112.137.171.29/Carfix/Case/TruckReject?key=%s&truckNo=%s&userCode=%s";

    private CarfixSQLiteOpenHelper dbHelper;

    private TruckDestination truckDestination;

    private ProgressDialog mProgress;

    public TaskAssignedActivity()
    {
        dbHelper = new CarfixSQLiteOpenHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_assigned);

        TextView detail = (TextView)findViewById(R.id.text_view);

        Intent intent = getIntent();

        final String key = intent.getStringExtra("key");
        final String truckNo = intent.getStringExtra("truckNo");
        final String userCode = intent.getStringExtra("userCode");
        String caseNo = intent.getStringExtra("caseNo");
        String vehRegNo = intent.getStringExtra("vehRegNo");
        String vehModel = intent.getStringExtra("vehModel");
        String phoneNo = intent.getStringExtra("phoneNo");
        String service = "Other";
        String breakdownAddress = intent.getStringExtra("breakdownAddress");
        double breakdownLatitude = intent.getDoubleExtra("breakdownLatitude", 0);
        double breakdownLongitude = intent.getDoubleExtra("breakdownLongitude", 0);
        String destinationAddress = intent.getStringExtra("destinationAddress");
        double destinationLatitude = intent.getDoubleExtra("destinationLatitude", 0);
        double destinationLongitude = intent.getDoubleExtra("destinationLongitude", 0);

        String[] choices = new String[]{"Accident", "Towing", "Change Battery", "Change Tyre", "Foreman Service", "Jump Start", "Other"};

        try
        {
            service = choices[intent.getIntExtra("service", 7) - 1];
        }
        catch (Exception e)
        {

        }

        detail.setText(String.format("New Job Assigned\n\nCase No : %s\nVehicle Reg. No. : %s\nVehicle Model : %s\nPhone No : %s\nService : %s\nBreakdown Location : \n%s\nDestination : \n%s",
                caseNo,
                vehRegNo,
                vehModel,
                phoneNo,
                service,
                breakdownAddress,
                destinationAddress));

        truckDestination = new TruckDestination();

        truckDestination.setCaseNo(caseNo);
        truckDestination.setRegNo(vehRegNo);
        truckDestination.setModel(vehModel);
        truckDestination.setPhoneNo(phoneNo);
        truckDestination.setService(service);
        truckDestination.setBreakdownAddress(breakdownAddress);
        truckDestination.setBreakdownLatitude(breakdownLatitude);
        truckDestination.setBreakdownLongitude(breakdownLongitude);
        truckDestination.setDestinationAddress(destinationAddress);
        truckDestination.setDestinationLatitude(destinationLatitude);
        truckDestination.setDestinationLongitude(destinationLongitude);

        Button acceptTask = (Button)findViewById(R.id.acceptTask);

        acceptTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitTruckAcceptRequest(key, truckNo, userCode);
                /*insertTruckDestination(truckDestination);

                showAlertDialog("Job Assigned", "Job accepted. (TODO: call server accept API)", new Runnable()
                {
                    public void run()
                    {
                        Handler handler = new Handler();

                        handler.postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                finish();
                            }
                        }, 1000);
                    }
                });*/
            }
        });

        Button rejectTask = (Button)findViewById(R.id.rejectTask);

        rejectTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitTruckRejectRequest(key, truckNo, userCode);
                /*showAlertDialog("Job Assigned", "Job rejected. (TODO: call server reject API)", new Runnable()
                {
                    public void run()
                    {
                        Handler handler = new Handler();

                        handler.postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                finish();
                            }
                        }, 1000);
                    }
                });*/


            }
        });
    }

    private void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.action_bar_main);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDefaultDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_task_assigned, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(String title, String message, final Runnable runnable)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();

                if (runnable != null)
                runnable.run();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void submitTruckAcceptRequest(String key, String truckNo, String userCode)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        String apiURL = String.format(TaskAssignedActivity.TRUCK_ACCEPT_API,
                key, truckNo, userCode);

        //Toast.makeText(TaskAssignedActivity.this, apiURL, Toast.LENGTH_LONG).show();

        asyncHttpClient.get(this, apiURL, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                String response = new String(arg2);

                if (response.equals("true"))
                {
                    insertTruckDestination(truckDestination);

                    showAlertDialog("Job Assigned", "Job accepted. Destination updated.", new Runnable()
                    {
                        public void run()
                        {
                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    finish();
                                }
                            }, 1000);
                        }
                    });
                }
                else
                {
                    showAlertDialog("Job Assigned", "Server error, invalid information submitted", null);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3)
            {
                Toast.makeText(TaskAssignedActivity.this, "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStart()
            {
                super.onStart();
                mProgress = new ProgressDialog(TaskAssignedActivity.this);
                mProgress.setMessage("Progressing... Please wait...");
                mProgress.setIndeterminate(false);
                mProgress.setCancelable(false);
                mProgress.show();
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
                mProgress.dismiss();
            }
        });
    }

    private void submitTruckRejectRequest(String key, String truckNo, String userCode)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        String apiURL = String.format(TaskAssignedActivity.TRUCK_REJECT_API,
                key, truckNo, userCode);

        //Toast.makeText(TaskAssignedActivity.this, apiURL, Toast.LENGTH_LONG).show();

        asyncHttpClient.get(this, apiURL, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                String response = new String(arg2);

                if (response.equals("true"))
                {
                    showAlertDialog("Job Rejected", "Job rejected.", new Runnable()
                    {
                        public void run()
                        {
                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    finish();
                                }
                            }, 1000);
                        }
                    });
                }
                else
                {
                    showAlertDialog("Job Assigned", "Server error, invalid information submitted", null);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3)
            {
                Toast.makeText(TaskAssignedActivity.this, "Something went wrong, Please try again",  Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStart()
            {
                super.onStart();
                mProgress = new ProgressDialog(TaskAssignedActivity.this);
                mProgress.setMessage("Progressing... Please wait...");
                mProgress.setIndeterminate(false);
                mProgress.setCancelable(false);
                mProgress.show();
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
                mProgress.dismiss();
            }
        });
    }

    public void insertTruckDestination(TruckDestination truckDestination)
    {
        dbHelper.getWritableDatabase().delete(TruckDestination.TABLE_NAME, null, null);
        dbHelper.getWritableDatabase().insert(TruckDestination.TABLE_NAME, null, truckDestination.toContentValues());
    }
}
