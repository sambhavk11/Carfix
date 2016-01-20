package my.carfix.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.carfix.carfix.model.Vehicle;
import my.carfix.carfix.R;

public class VehicleAdapter extends ArrayAdapter<Vehicle>
{
    private List<Vehicle> vehicles;

    private Context context;

    public VehicleAdapter(Context context, int resource, List<Vehicle> vehicles)
    {
        super(context, resource, vehicles);

        this.vehicles = vehicles;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Vehicle vehicle = vehicles.get(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.vehicle_row, parent, false);

            TextView regnoTextView = (TextView)convertView.findViewById(R.id.text_view_regno);

            regnoTextView.setText(vehicle.getRegNo());

            TextView modelTextView = (TextView)convertView.findViewById(R.id.text_view_model);

            modelTextView.setText("Vehicle Model : " + (vehicle.getModel().length() > 0 ? vehicle.getModel() : "-"));
        }

        return convertView;
    }
}
