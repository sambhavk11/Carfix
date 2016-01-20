package my.carfix.carfix.fragment;

import java.util.*;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import my.carfix.adapter.VehicleAdapter;
import my.carfix.carfix.model.Vehicle;
import my.carfix.carfix.R;

public class VehicleListFragment extends Fragment implements ListView.OnItemClickListener
{
    private List<Vehicle> vehicles;

    private ListView listView;

    private VehicleListFragmentCallbacks callbacks;

    public static VehicleListFragment newInstance()
    {
        return new VehicleListFragment();
    }

    public VehicleListFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_vehicle_list, container, false);

        listView = (ListView)view.findViewById(R.id.list_view);

        vehicles = callbacks.fetchVehicles();

        //listView.setAdapter(new ArrayAdapter<Vehicle>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, vehicles));
        listView.setAdapter(new VehicleAdapter(getActivity(), R.id.list_view, vehicles));
        listView.setOnItemClickListener(this);

        Button addVehicleButton = (Button)view.findViewById(R.id.add_vehicle);

        addVehicleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (callbacks != null)
                {
                    callbacks.onAddVehicle();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        TextView actionBarTitleTextView = (TextView)actionBar.getCustomView().findViewById(R.id.text_view_action_bar_title);
        actionBarTitleTextView.setText("Carfix");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            callbacks = (VehicleListFragmentCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement VehicleListFragmentCallbacks");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        callbacks = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (callbacks != null)
        {
            callbacks.gotoVehicleFragment(vehicles.get(position));
        }
    }

    public interface VehicleListFragmentCallbacks
    {
        public List<Vehicle> fetchVehicles();

        public void gotoVehicleFragment(Vehicle vehicle);

        public void onAddVehicle();
    }
}
