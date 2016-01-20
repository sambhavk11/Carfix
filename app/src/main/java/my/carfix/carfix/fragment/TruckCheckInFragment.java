package my.carfix.carfix.fragment;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import
        android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import my.carfix.carfix.LocationAddress;
import my.carfix.carfix.R;
import my.carfix.carfix.SoftKeyboard;
import my.carfix.carfix.activity.SplashScreenActivity;
import my.carfix.carfix.model.TruckDestination;
import my.carfix.carfix.service.TruckCheckInService;
import my.carfix.datastructure.WorkshopsData;
import my.carfix.carfix.model.Truck;

public class TruckCheckInFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback, LocationListener
{
    public static final String PRODUCTION_API_URL = "";

    public static final String DEVELOPMENT_API_URL = "http://112.137.171.29/Carfix/CheckIn?truckNo=%s&userCode=%s&latitude=%s&longitude=%s&address=%s";

    public static final String PRODUCTION_API_URL2 = "";

    public static final String DEVELOPMENT_API_URL2 = "http://54.179.190.147:8080/CarfixWebService/RegisterTruckDeviceID?UserCode=%s&TruckNo=%s&DeviceID=%s";

    public static final long AUTO_CHECK_IN_DURATION = 300000;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private Truck truck;

    private TruckCheckInFragmentCallbacks callbacks;

    private GoogleApiClient googleApiClient;

    private GoogleMap googleMap;

    private double latitude = 0;

    private double longitude = 0;

    private boolean isUpdated = false;

    private ProgressDialog mProgress;

    private long nextUpdateTime;

    private CountDownTimer countDownTimer;

    public static TruckCheckInFragment newInstance(Truck truck)
    {
        TruckCheckInFragment fragment = new TruckCheckInFragment();

        fragment.truck = truck;

        return fragment;
    }

    public TruckCheckInFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truck_check_in, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        TextView truckNoTextView = (TextView) view.findViewById(R.id.tv_truck_no);
        TextView userCodeTextView = (TextView) view.findViewById(R.id.tv_user_code);
        TextView addressTextView = (TextView) view.findViewById(R.id.tv_address);

        final EditText truckNoEditText = (EditText) view.findViewById(R.id.et_truck_no);
        final EditText userCodeEditText = (EditText) view.findViewById(R.id.et_user_code);
        final EditText addressEditText = (EditText) view.findViewById(R.id.et_address);

        addressEditText.setHorizontallyScrolling(false);
        addressEditText.setMaxLines(Integer.MAX_VALUE);

        truckNoTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                truckNoEditText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(truckNoEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        userCodeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userCodeEditText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userCodeEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        addressTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addressEditText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(addressEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        if (truck != null)
        {
            truckNoEditText.setText(truck.getTruckNo());

            if (truck.getAutoCheckInTime() > 0)
            {
                ScrollView checkInScrollView = (ScrollView)view.findViewById(R.id.sv_check_in);
                checkInScrollView.setVisibility(View.GONE);

                ScrollView stopCheckInScrollView = (ScrollView)view.findViewById(R.id.sv_stop_check_in);
                stopCheckInScrollView.setVisibility(View.VISIBLE);
            }
        }

        /*Button jobInfoButton = (Button)view.findViewById(R.id.job_info);

        jobInfoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TruckDestination truckDestination = callbacks.fetchTruckDestination();

                if (truckDestination != null)
                {
                    showAlertDialog("Job Info", String.format("Vehicle No : %s\nPhone No : %s\nBreakdown Location : %s\n",
                            truckDestination.getRegNo(),
                            truckDestination.getPhoneNo(),
                            truckDestination.getBreakdownAddress()));
                }
                else
                {
                    showAlertDialog("Job Info", String.format("Last Accepted Job Not Found."));
                }
            }
        });*/

        Button breakdownAddressNavigateButton = (Button)view.findViewById(R.id.breakdown_address_navigate);

        breakdownAddressNavigateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TruckDestination truckDestination = callbacks.fetchTruckDestination();

                if (truckDestination != null && truckDestination.getBreakdownAddress().length() > 0)
                {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s", latitude, longitude, truckDestination.getBreakdownLatitude(), truckDestination.getBreakdownLongitude())));
                    startActivity(intent);
                }
                else
                {
                    showAlertDialog("Job Info", String.format("Breakdown Location Not Found."));
                }
            }
        });

        Button destinationAddressNavigateButton = (Button)view.findViewById(R.id.destination_address_navigate);

        destinationAddressNavigateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TruckDestination truckDestination = callbacks.fetchTruckDestination();

                if (truckDestination != null && truckDestination.getDestinationAddress().length() > 0)
                {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s", latitude, longitude, truckDestination.getDestinationLatitude(), truckDestination.getDestinationLongitude())));
                    startActivity(intent);
                }
                else
                {
                    showAlertDialog("Job Info", String.format("Destination Not Found."));
                }
            }
        });

        Button stopCheckInButton = (Button)view.findViewById(R.id.stop_check_in);

        stopCheckInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ScrollView checkInScrollView = (ScrollView)getView().findViewById(R.id.sv_check_in);
                checkInScrollView.setVisibility(View.VISIBLE);

                ScrollView stopCheckInScrollView = (ScrollView)getView().findViewById(R.id.sv_stop_check_in);
                stopCheckInScrollView.setVisibility(View.GONE);

                callbacks.deleteTruck(truck);

                if (countDownTimer != null)
                {
                    countDownTimer.cancel();

                    countDownTimer = null;
                }

                restoreActionBar();
            }
        });

        TruckDestination truckDestination = callbacks.fetchTruckDestination();

        if (truckDestination != null)
        {
            TextView jobInfoTextView = (TextView) view.findViewById(R.id.text_view_job_info);
            jobInfoTextView.setText(truckDestination.getRegNo() + " - Case #" + truckDestination.getCaseNo());

            TextView breakdownLocationTextView = (TextView) view.findViewById(R.id.text_view_breakdown_location);
            breakdownLocationTextView.setHorizontallyScrolling(false);
            breakdownLocationTextView.setMaxLines(Integer.MAX_VALUE);
            breakdownLocationTextView.setText(truckDestination.getBreakdownAddress());

            TextView destinationTextView = (TextView) view.findViewById(R.id.text_view_destination);
            destinationTextView.setHorizontallyScrolling(false);
            destinationTextView.setMaxLines(Integer.MAX_VALUE);
            destinationTextView.setText(truckDestination.getDestinationAddress());
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            callbacks = (TruckCheckInFragmentCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement TruckCheckInFragmentCallbacks");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        callbacks = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        googleApiClient.connect();
        if (truck != null)
        {
            nextUpdateTime = truck.getAutoCheckInTime();

            while (nextUpdateTime < System.currentTimeMillis())
            {
                nextUpdateTime += AUTO_CHECK_IN_DURATION;
            }

            countDownTimer = new CountDownTimer(nextUpdateTime - System.currentTimeMillis(), 1000)
            {
                public void onTick(long millisUntilFinished)
                {
                    EditText countdownEditText = (EditText)getView().findViewById(R.id.et_countdown);
                    countdownEditText.setText(String.format("%ds", millisUntilFinished / 1000));
                }

                public void onFinish()
                {
                    countDownTimer = new CountDownTimer(AUTO_CHECK_IN_DURATION, 1000)
                    {
                        public void onTick(long millisUntilFinished)
                        {
                            EditText countdownEditText = (EditText)getView().findViewById(R.id.et_countdown);
                            countdownEditText.setText(String.format("%ds", millisUntilFinished / 1000));
                        }

                        public void onFinish()
                        {
                            start();
                        }
                    }.start();
                }
            }.start();
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        googleApiClient.disconnect();
        isUpdated = false;
        if (countDownTimer != null)
        {
            countDownTimer.cancel();

            countDownTimer = null;
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onConnected(Bundle arg0)
    {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, REQUEST, this);
    }

    @Override
    public void onConnectionSuspended(int arg0)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0)
    {

    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        googleMap = map;
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(googleMap != null){
            if(isUpdated ==false ){
                isUpdated = true;
                LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(position)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                                //   .bearing(90)                // Sets the orientation of the camera to east
                                //   .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(),getActivity(), new GeocoderHandler());

                //googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            }
        }
    }

    private boolean validateInputs(String trunkNo, String userCode)
    {
        if (trunkNo.equals(""))
        {
            showAlertDialog("Oops", "Trunk No is required");

            return false;
        }
        else if (userCode.equals(""))
        {
            showAlertDialog("Oops", "User Code is required");

            return false;
        }

        return true;
    }

    private void restoreActionBar()
    {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

        actionBar.setCustomView(R.layout.action_bar_truck_check_in);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        if (countDownTimer != null)
        {
            Button sendButton = (Button) actionBar.getCustomView().findViewById(R.id.send);

            sendButton.setVisibility(View.INVISIBLE);
        }
        else {
            Button sendButton = (Button) actionBar.getCustomView().findViewById(R.id.send);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText truckNoEditText = (EditText) getView().findViewById(R.id.et_truck_no);
                    EditText userCodeEditText = (EditText) getView().findViewById(R.id.et_user_code);
                    EditText addressEditText = (EditText) getView().findViewById(R.id.et_address);

                    if (validateInputs(truckNoEditText.getText().toString().trim(), userCodeEditText.getText().toString().trim())) {
                        submitTruckCheckInRequest(truckNoEditText.getText().toString().trim(), userCodeEditText.getText().toString().trim(), addressEditText.getText().toString().trim());

                        hideVirtualKeyboard();
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        restoreActionBar();

        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        TextView actionBarTitleTextView = (TextView)actionBar.getCustomView().findViewById(R.id.text_view_action_bar_title);
        actionBarTitleTextView.setText("Truck Check In");

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void submitTruckCheckInRequest(final String truckNo, final String userCode, String address)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        String apiURL = String.format(DEVELOPMENT_API_URL, truckNo, userCode, latitude, longitude, address);

        asyncHttpClient.get(getActivity(), apiURL, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                String response = new String(arg2);

                // TODO remove this
                if (truckNo.equals("QWER1234") && userCode.equals("123456"))
                {
                    response = "true";
                }

                if (response.equals("true"))
                {
                    ScrollView checkInScrollView = (ScrollView)getView().findViewById(R.id.sv_check_in);
                    checkInScrollView.setVisibility(View.GONE);

                    ScrollView stopCheckInScrollView = (ScrollView)getView().findViewById(R.id.sv_stop_check_in);
                    stopCheckInScrollView.setVisibility(View.VISIBLE);

                    truck = new Truck();

                    truck.setTruckNo(truckNo);
                    truck.setUserCode(userCode);
                    truck.setAutoCheckInTime(System.currentTimeMillis() + AUTO_CHECK_IN_DURATION);

                    callbacks.insertTruck(truck);

                    startAlarmService(truck);

                    showAlertDialog("Truck Check In", "Truck check in success, auto update truck location to server every 5 minutes");

                    registerCaseDeviceID(userCode, truckNo);

                    nextUpdateTime = truck.getAutoCheckInTime();

                    countDownTimer = new CountDownTimer(AUTO_CHECK_IN_DURATION, 1000)
                    {
                        public void onTick(long millisUntilFinished)
                        {
                            EditText countdownEditText = (EditText)getView().findViewById(R.id.et_countdown);
                            countdownEditText.setText(String.format("%ds", millisUntilFinished / 1000));
                        }

                        public void onFinish()
                        {
                            start();
                        }
                    }.start();

                    restoreActionBar();
                }
                else
                {
                    showAlertDialog("Truck Check In", "Truck check in fail, invalid truck no or user code");
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3)
            {
                Toast.makeText(getActivity(), "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStart()
            {
                super.onStart();
                mProgress = new ProgressDialog(getActivity());
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

    private void registerCaseDeviceID(String userCode, String truckNo)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        String apiURL = String.format(DEVELOPMENT_API_URL2, userCode, truckNo, SplashScreenActivity.deviceID);

        asyncHttpClient.get(getActivity(), apiURL, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {

            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3)
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
    }

    public void hideVirtualKeyboard()
    {
        Activity activity = getActivity();

        View view = activity.getWindow().getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showAlertDialog(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void startAlarmService(Truck truck)
    {
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), TruckCheckInService.class);

        intent.putExtra("userCode", truck.getUserCode());
        intent.putExtra("truckNo", truck.getTruckNo());
        intent.putExtra("autoCheckInTime", truck.getAutoCheckInTime());

        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), (int)(System.currentTimeMillis() % Integer.MAX_VALUE), intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, truck.getAutoCheckInTime(), pendingIntent);
    }

    private void removeAlarmService()
    {

    }

    private class GeocoderHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            String locationAddress;
            switch (message.what)
            {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            EditText addressEditText = (EditText) getView().findViewById(R.id.et_address);
            addressEditText.setText(locationAddress);
        }
    }

    public interface TruckCheckInFragmentCallbacks
    {
        public void insertTruck(Truck truck);

        public void deleteTruck(Truck truck);

        public TruckDestination fetchTruckDestination();
    }
}
