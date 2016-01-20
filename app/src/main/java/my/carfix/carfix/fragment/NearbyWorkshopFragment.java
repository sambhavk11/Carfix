package my.carfix.carfix.fragment;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import my.carfix.carfix.LocationAddress;
import my.carfix.carfix.R;
import my.carfix.carfix.activity.MainDrawerActivity;
import my.carfix.datastructure.CarFixData;
import my.carfix.datastructure.WorkshopData;
import my.carfix.datastructure.WorkshopsData;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyWorkshopFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener
{
    public static final String PRODUCTION_API_URL = "";

    public static final String DEVELOPMENT_API_URL = "http://112.137.171.29/Carfix/Map/GetNearbyWorkshops?latitude=%s&longitude=%s&distanceInKM=%d";

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private GoogleApiClient googleApiClient;

    private GoogleMap googleMap;

    private double latitude = 0;

    private double longitude = 0;

    private boolean isUpdated = false;

    private ProgressDialog mProgress;

    private LinkedHashMap<String, WorkshopData> markers = new LinkedHashMap<String, WorkshopData>();

    private WorkshopData lastSelectedWorkshop;

    public static NearbyWorkshopFragment newInstance()
    {
        return new NearbyWorkshopFragment();
    }

    public NearbyWorkshopFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        TextView actionBarTitleTextView = (TextView)actionBar.getCustomView().findViewById(R.id.text_view_action_bar_title);
        actionBarTitleTextView.setText("Nearby Workshop");

        super.onCreateOptionsMenu(menu, inflater);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearby_workshop, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null)
        {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        Button startNavigationButton = (Button)view.findViewById(R.id.btn_navigation);

        startNavigationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lastSelectedWorkshop == null)

                {
                    showAlertDialog("Oops", "Workshop not selected");
                }
                else
                {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s", latitude, longitude, lastSelectedWorkshop.Latitude, lastSelectedWorkshop.Longitude)));
                    startActivity(intent);
                }
            }
        });
        return view;
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
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMarkerClickListener(this);
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
                submitNearbyWorkshopRequest();
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        TextView companyNameTextView = (TextView) getView().findViewById(R.id.text_view_company_name);
        companyNameTextView.setText(markers.get(marker.getId()).CompanyName);
        lastSelectedWorkshop = markers.get(marker.getId());
        return true;
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(latitude, longitude, getActivity(), new GeocoderHandler());
        Toast.makeText(getActivity(), "Address Updated", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void refreshMarkers(WorkshopsData data)
    {
        googleMap.clear();
        markers.clear();
        for (WorkshopData workshop : data.workshops)
        {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(workshop.Latitude, workshop.Longitude)).title(workshop.CompanyName));
            markers.put(marker.getId(), workshop);
        }
    }

    private void submitNearbyWorkshopRequest()
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        String apiURL = String.format(NearbyWorkshopFragment.DEVELOPMENT_API_URL,
                latitude, longitude, 5);

        asyncHttpClient.get(getActivity(), apiURL, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                String response = new String(arg2);
                WorkshopsData workshopsData = WorkshopsData.get(response);
                if(workshopsData != null)
                {
                    refreshMarkers(workshopsData);
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
        }
    }
}
