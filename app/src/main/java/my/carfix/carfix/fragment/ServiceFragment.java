package my.carfix.carfix.fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

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
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import my.carfix.carfix.LocationAddress;
import my.carfix.carfix.activity.MainDrawerActivity;
import my.carfix.carfix.model.Vehicle;
import my.carfix.carfix.R;
import my.carfix.datastructure.CarFixData;
import my.carfix.datastructure.PoliciesData;

public class ServiceFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener
{
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private Vehicle vehicle;

    private ServiceFragmentCallbacks callbacks;

    private GoogleApiClient googleApiClient;

    private GoogleMap googleMap;

    private double latitude = 0;

    private double longitude = 0;

    private boolean isUpdated = false;

    private ProgressDialog mProgress;

    public static ServiceFragment newInstance(Vehicle vehicle)
    {
        ServiceFragment fragment = new ServiceFragment();

        fragment.vehicle = vehicle;

        return fragment;
    }

    public ServiceFragment()
    {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null)
        {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        TextView addressTextView = (TextView) view.findViewById(R.id.tv_address);

        final EditText addressEditText = (EditText) view.findViewById(R.id.et_address);

        addressEditText.setHorizontallyScrolling(false);
        addressEditText.setMaxLines(Integer.MAX_VALUE);

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

        return view;
    }

    private int getServiceNo(String service)
    {
        String[] choices = new String[]{"Accident", "Towing", "Change Battery", "Change Tyre", "Foreman Service", "Jump Start", "Other"};

        for (int index = 0; index < choices.length; index++)
        {
            if (choices[index].equals(service))
            {
                return index + 1;
            }
        }

        return 0;
    }
    public void gotoPolicyFragment(CarFixData carFixData)
    {
        if (carFixData.PolicyID == 0 && carFixData.MatchedPolicies.size() > 0)
        {
            if (callbacks != null)
            {
                callbacks.gotoPolicyListFragment(carFixData, 1);
            }
        }
        else if (carFixData.PolicyID == 0 && carFixData.MatchedPolicies.size() == 0)
        {
            if (callbacks != null)
            {
                callbacks.gotoPolicyListFragment(carFixData, 2);
            }
        }
        else if (carFixData.PolicyID > 0 && carFixData.MatchedPolicies.size() == 1)
        {
            PoliciesData policiesData = carFixData.MatchedPolicies.get(0);

            if (callbacks != null)
            {
                callbacks.gotoPolicyFragment(policiesData.Logo, policiesData.SubscriberName, policiesData.Hotline, carFixData.PhoneNo, carFixData.Passcode);
            }
        }
    }

    private void submitRequest()
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        EditText addressEditText = (EditText) getView().findViewById(R.id.et_address);
        String address = addressEditText.getText().toString().trim();

        String apiURL = String.format(MainDrawerActivity.DEVELOPMENT_API_URL,
                vehicle.getRegNo(), vehicle.getPhoneNo(), address,
                latitude, longitude, vehicle.getModel(), getServiceNo(vehicle.getService()));

        asyncHttpClient.get(getActivity(), apiURL, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                String response = new String(arg2);
                CarFixData carFixData = CarFixData.get(response);
                if(carFixData != null)
                {
                    gotoPolicyFragment(carFixData);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3)
            {
                Toast.makeText(getActivity(), "Something went wrong, Please try again",  Toast.LENGTH_LONG).show();
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

    public void showAlertDialog(String title, String message)
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

    private void restoreActionBar()
    {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

        actionBar.setCustomView(R.layout.action_bar_service);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Button sendButton = (Button)actionBar.getCustomView().findViewById(R.id.send);

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText addressEditText = (EditText) getView().findViewById(R.id.et_address);

                if (addressEditText.getText().toString().trim().equals(""))
                {
                    showAlertDialog("Oops", "Address is required");
                }
                else
                {
                    submitRequest();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        restoreActionBar();

        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        TextView actionBarTitleTextView = (TextView)actionBar.getCustomView().findViewById(R.id.text_view_action_bar_title);
        actionBarTitleTextView.setText("Carfix");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        googleApiClient.disconnect();
        isUpdated = false;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            callbacks = (ServiceFragmentCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement ServiceFragmentCallbacks");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        callbacks = null;
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
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        googleMap = map;
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(latitude, longitude, getActivity(), new GeocoderHandler());
        Toast.makeText(getActivity(), "Address Updated", Toast.LENGTH_SHORT).show();
        return false;
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

    public interface ServiceFragmentCallbacks
    {
        public void gotoPolicyListFragment(CarFixData policies, int policyType);

        public void gotoPolicyFragment(String policyImage, String policyName, String policyHotline, String userPhoneNo, String passcode);
    }
}
