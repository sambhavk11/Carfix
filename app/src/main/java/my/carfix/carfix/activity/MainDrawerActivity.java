package my.carfix.carfix.activity;

import java.util.*;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import my.carfix.carfix.fragment.CaseFragment;
import my.carfix.carfix.fragment.CaseListFragment;
import my.carfix.carfix.fragment.PolicyFragment;
import my.carfix.carfix.fragment.PolicyListFragment;
import my.carfix.carfix.content.CarfixSQLiteOpenHelper;
import my.carfix.carfix.fragment.NavigationDrawerFragment;
import my.carfix.carfix.fragment.TruckCheckInFragment;
import my.carfix.carfix.fragment.VehicleFragment;
import my.carfix.carfix.fragment.VehicleListFragment;
import my.carfix.carfix.fragment.ServiceFragment;
import my.carfix.carfix.fragment.NearbyWorkshopFragment;
import my.carfix.carfix.fragment.AboutUsFragment;
import my.carfix.carfix.fragment.ContactUsFragment;
import my.carfix.carfix.fragment.TermsAndConditionsFragment;
import my.carfix.carfix.fragment.PrivacyPolicyFragment;
import my.carfix.carfix.model.LogCase;
import my.carfix.carfix.model.LogCaseMessage;
import my.carfix.carfix.model.Truck;
import my.carfix.carfix.model.TruckDestination;
import my.carfix.carfix.model.Vehicle;
import my.carfix.carfix.R;
import my.carfix.datastructure.CarFixData;

public class MainDrawerActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        VehicleListFragment.VehicleListFragmentCallbacks, VehicleFragment.VehicleFragmentCallbacks,
        ServiceFragment.ServiceFragmentCallbacks, PolicyListFragment.PolicyListFragmentCallbacks,
        CaseListFragment.CaseListFragmentCallbacks, CaseFragment.CaseFragmentCallbacks,
        TruckCheckInFragment.TruckCheckInFragmentCallbacks
{
    public static final String PRODUCTION_API_URL = "http://www.carfix.my/MotorAssist/LogCase?VehReg=%s&PhoneNo=%s&Address=%s&Latitude=%s&Longitude=%s&VehModel=%s&ServiceNeeded=%s";

    public static final String DEVELOPMENT_API_URL = "http://112.137.171.29/Carfix/MotorAssist/LogCase?VehReg=%s&PhoneNo=%s&Address=%s&Latitude=%s&Longitude=%s&VehModel=%s&ServiceNeeded=%s";

    public static final String PRODUCTION_API_URL2 = PRODUCTION_API_URL + "&PolicyID=%d";

    public static final String DEVELOPMENT_API_URL2 = DEVELOPMENT_API_URL + "&PolicyID=%d";

    private CarfixSQLiteOpenHelper dbHelper;

    private NavigationDrawerFragment navigationDrawerFragment;

    private DrawerPosition lastDrawerPosition = DrawerPosition.HOME;

    private HomeFragment lastHomeFragment = HomeFragment.VEHICLE_LIST;

    public MainDrawerActivity()
    {
        dbHelper = new CarfixSQLiteOpenHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        navigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
        //navigationDrawerFragment.getDrawerToggle().setDrawerIndicatorEnabled(false);

        if (getIntent() != null)
        {
            if (getIntent().getStringExtra("caseNo") != null)
            {
                String caseNo = getIntent().getStringExtra("caseNo");

                List<LogCase> logCases = fetchLogCases();

                for (LogCase logCase : logCases)
                {
                    if (logCase.getCaseNo().equals(caseNo))
                    {
                        gotoCaseFragment(logCase);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DrawerPosition drawerPosition = DrawerPosition.HOME;
        try
        {
            drawerPosition = DrawerPosition.values()[position];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            ;
        }

        switch (drawerPosition)
        {
            case HOME:
                while (fragmentManager.popBackStackImmediate());

                List<Vehicle> vehicle = fetchVehicles();

                if (vehicle.size() > 0)
                {
                    fragmentTransaction.replace(R.id.container, VehicleListFragment.newInstance());
                }
                else
                {
                    fragmentTransaction.replace(R.id.container, VehicleFragment.newInstance());
                }

                lastHomeFragment = HomeFragment.VEHICLE_LIST;

                break;

            case NEARBY_WORKSHOP:
                fragmentTransaction.replace(R.id.container, NearbyWorkshopFragment.newInstance());

                /*if (lastDrawerPosition == DrawerPosition.HOME)
                {
                    fragmentTransaction.addToBackStack(null);
                }*/
                break;

            case CASE_HISTORY:
                fragmentTransaction.replace(R.id.container, CaseListFragment.newInstance());
                break;

            case TRUCK_CHECK_IN:
                Truck truck = fetchTruck();

                fragmentTransaction.replace(R.id.container, TruckCheckInFragment.newInstance(truck));
                break;

            case CONTACT_US:
                fragmentTransaction.replace(R.id.container, ContactUsFragment.newInstance());

                if (lastDrawerPosition == DrawerPosition.HOME)
                {
                    fragmentTransaction.addToBackStack(null);
                }
                break;

            case ABOUT_US:
                fragmentTransaction.replace(R.id.container, AboutUsFragment.newInstance());

                if (lastDrawerPosition == DrawerPosition.HOME)
                {
                    fragmentTransaction.addToBackStack(null);
                }
                break;

            case TERMS_AND_CONDITIONS:
                fragmentTransaction.replace(R.id.container, TermsAndConditionsFragment.newInstance());

                if (lastDrawerPosition == DrawerPosition.HOME)
                {
                    fragmentTransaction.addToBackStack(null);
                }
                break;

            case PRIVACY_POLICY:
                fragmentTransaction.replace(R.id.container, PrivacyPolicyFragment.newInstance());

                if (lastDrawerPosition == DrawerPosition.HOME)
                {
                    fragmentTransaction.addToBackStack(null);
                }
                break;
        }

        fragmentTransaction.commit();

        lastDrawerPosition = drawerPosition;
    }

    @Override
    public void onBackPressed()
    {
        if (lastDrawerPosition != DrawerPosition.HOME)
        {
            onNavigationDrawerItemSelected(0);
        }
        else if (lastHomeFragment != HomeFragment.VEHICLE_LIST && lastHomeFragment != HomeFragment.CASE_LIST)
        {
            onNavigationDrawerItemSelected(0);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.action_bar_main);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDefaultDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<Vehicle> fetchVehicles()
    {
        Cursor cursor = dbHelper.getReadableDatabase().query(Vehicle.TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();

        while (cursor.moveToNext())
        {
            Vehicle vehicle = new Vehicle();
            vehicle.loadCursor(cursor);

            vehicles.add(vehicle);
        }

        return vehicles;
    }

    public Vehicle fetchVehicle(String _ID)
    {
        Cursor cursor = dbHelper.getReadableDatabase().query(Vehicle.TABLE_NAME, null, "_ID = ?", new String[]{_ID}, null, null, null);

        if (cursor.getCount() == 1)
        {
            cursor.moveToNext();

            Vehicle vehicle = new Vehicle();
            vehicle.loadCursor(cursor);

            return vehicle;
        }

        cursor.close();

        return null;
    }

    @Override
    public void insertVehicle(Vehicle vehicle)
    {
        dbHelper.getWritableDatabase().insert(Vehicle.TABLE_NAME, null, vehicle.toContentValues());
    }

    @Override
    public void updateVehicle(Vehicle vehicle)
    {
        dbHelper.getWritableDatabase().update(Vehicle.TABLE_NAME, vehicle.toContentValues(), "_ID = ?", new String[]{vehicle.get_id()});
    }

    @Override
    public void deleteVehicle(Vehicle vehicle)
    {
        dbHelper.getWritableDatabase().delete(Vehicle.TABLE_NAME, "_ID = ?", new String[]{vehicle.get_id()});
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

    @Override
    public void insertTruck(Truck truck)
    {
        dbHelper.getWritableDatabase().insert(Truck.TABLE_NAME, null, truck.toContentValues());
    }

    @Override
    public void deleteTruck(Truck truck)
    {
        dbHelper.getWritableDatabase().delete(Truck.TABLE_NAME, null, null);
        //dbHelper.getWritableDatabase().delete(Truck.TABLE_NAME, "_ID = ?", new String[]{truck.get_id()});
    }

    @Override
    public TruckDestination fetchTruckDestination()
    {
        Cursor cursor = dbHelper.getReadableDatabase().query(TruckDestination.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToNext())
        {
            TruckDestination truckDestination = new TruckDestination();
            truckDestination.loadCursor(cursor);

            return truckDestination;
        }

        return null;
    }

    @Override
    public List<LogCase> fetchLogCases()
    {
        Cursor cursor = dbHelper.getReadableDatabase().query(LogCase.TABLE_NAME, null, null, null, null, null, "_id desc");

        ArrayList<LogCase> logCases = new ArrayList<LogCase>();

        while (cursor.moveToNext())
        {
            LogCase logCase = new LogCase();
            logCase.loadCursor(cursor);

            logCases.add(logCase);
        }

        return logCases;
    }

    @Override
    public List<LogCaseMessage> fetchLogCaseMessages(String caseNo)
    {
        Cursor cursor = dbHelper.getReadableDatabase().query(LogCaseMessage.TABLE_NAME, null, "caseNo = ?", new String[]{caseNo}, null, null, "_id desc");

        ArrayList<LogCaseMessage> logCaseMessages = new ArrayList<LogCaseMessage>();

        while (cursor.moveToNext())
        {
            LogCaseMessage logCaseMessage = new LogCaseMessage();
            logCaseMessage.loadCursor(cursor);

            logCaseMessages.add(logCaseMessage);
        }

        return logCaseMessages;
    }

    @Override
    public void gotoServiceFragment(Vehicle vehicle, boolean addToBackStack)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, ServiceFragment.newInstance(vehicle));

        if (addToBackStack)
        {
            fragmentTransaction.addToBackStack(null);
        }
        else
        {
            lastHomeFragment = HomeFragment.SERVICE;
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onAddVehicle()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, VehicleFragment.newInstance());

        fragmentTransaction.commit();

        lastHomeFragment = HomeFragment.VEHICLE;
    }

    @Override
    public void gotoVehicleListFragment()
    {
        onNavigationDrawerItemSelected(0);

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, VehicleListFragment.newInstance());

        fragmentTransaction.commit();*/
    }

    @Override
    public void gotoVehicleFragment(Vehicle vehicle)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, VehicleFragment.newInstance(vehicle));

        fragmentTransaction.commit();

        lastHomeFragment = HomeFragment.VEHICLE;
    }

    @Override
    public void gotoPolicyFragment(String policyImage, String policyName, String policyHotline, String userPhoneNo, String passcode)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, PolicyFragment.newInstance(policyImage, policyName, policyHotline, userPhoneNo, passcode));

        fragmentTransaction.commit();

        lastHomeFragment = HomeFragment.POLICY;
    }

    @Override
    public void gotoPolicyListFragment(CarFixData policies, int policyType)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, PolicyListFragment.newInstance(policies, policyType));

        fragmentTransaction.commit();

        lastHomeFragment = HomeFragment.POLICY_LIST;
    }

    @Override
    public void gotoCaseFragment(LogCase logCase)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, CaseFragment.newInstance(logCase));

        fragmentTransaction.commit();

        lastHomeFragment = HomeFragment.CASE_LIST;
    }
}
