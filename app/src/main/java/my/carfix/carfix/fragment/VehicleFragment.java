package my.carfix.carfix.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import my.carfix.carfix.activity.MainDrawerActivity;
import my.carfix.carfix.activity.SplashScreenActivity;
import my.carfix.carfix.model.Vehicle;
import my.carfix.carfix.R;
import my.carfix.datastructure.CarFixData;

public class VehicleFragment extends Fragment
{
    private Vehicle vehicle;

    private Button deleteVehicleButton;

    private VehicleFragmentCallbacks callbacks;

    private ProgressDialog mProgress;

    public static VehicleFragment newInstance()
    {
        return new VehicleFragment();
    }

    public static VehicleFragment newInstance(Vehicle vehicle)
    {
        VehicleFragment fragment = new VehicleFragment();

        fragment.vehicle = vehicle;

        return fragment;
    }

    public VehicleFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);

        TextView regNoTextView = (TextView) view.findViewById(R.id.tv_vehicle_regno);
        TextView modelTextView = (TextView) view.findViewById(R.id.tv_vehicle_model);
        TextView phoneNoTextView = (TextView) view.findViewById(R.id.tv_phoneno);
        TextView serviceTextView = (TextView) view.findViewById(R.id.tv_service);

        final EditText regNoEditText = (EditText) view.findViewById(R.id.et_vehicle_regno);
        final EditText modelEditText = (EditText) view.findViewById(R.id.et_vehicle_model);
        final EditText phoneNoEditText = (EditText) view.findViewById(R.id.et_phoneno);
        final EditText serviceEditText = (EditText) view.findViewById(R.id.et_service);

        regNoTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                regNoEditText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(regNoEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        modelTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                modelEditText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(modelEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        phoneNoTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                phoneNoEditText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(phoneNoEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        serviceTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showServiceDialog();
            }
        });

        if (vehicle != null)
        {
            regNoEditText.getText().append(vehicle.getRegNo());
            modelEditText.getText().append(vehicle.getModel());
            phoneNoEditText.getText().append(vehicle.getPhoneNo());
            serviceEditText.getText().append(vehicle.getService());
        }

        phoneNoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_NEXT)
                {
                    showServiceDialog();
                    return false;
                }
                return false;
            }
        });

        serviceEditText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showServiceDialog();
            }
        });

        deleteVehicleButton = (Button)view.findViewById(R.id.delete_vehicle);

        deleteVehicleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (callbacks != null)
                {
                    callbacks.deleteVehicle(vehicle);

                    deleteVehicleButton.setVisibility(View.INVISIBLE);

                    Toast.makeText(getActivity(), "Vehicle Deleted", Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            callbacks.gotoVehicleListFragment();
                        }
                    }, 1000);
                }
            }
        });

        if (vehicle != null)
        {
            deleteVehicleButton.setVisibility(View.VISIBLE);
        }

        return view;
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

    public void showServiceDialog()
    {
		hideVirtualKeyboard();

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
		builderSingle.setTitle("Service:-");
		String[] choices = new String[]{"Accident", "Towing", "Change Battery", "Change Tyre", "Foreman Service", "Jump Start", "Other"};
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),  android.R.layout.select_dialog_item, choices);

		builderSingle.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
            {
                EditText serviceEditText = (EditText) getView().findViewById(R.id.et_service);

                serviceEditText.setText("");
			}
		});

		builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
            {
                EditText serviceEditText = (EditText) getView().findViewById(R.id.et_service);

                serviceEditText.setText(arrayAdapter.getItem(which));
			}
		});

		builderSingle.show();
    }

    private boolean validateInputs(String regNo, String phoneNo)
    {
        if (regNo.equals(""))
        {
            showAlertDialog("Oops", "Vehicle Reg. No is required");

            return false;
        }
        else if (phoneNo.equals(""))
        {
            showAlertDialog("Oops", "Phone No is required");

            return false;
        }

        return true;
    }

    private void restoreActionBar()
    {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        if (vehicle == null)
        {
            actionBar.setCustomView(R.layout.action_bar_add_vehicle);
        }
        else
        {
            actionBar.setCustomView(R.layout.action_bar_edit_vehicle);
        }
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        if (vehicle == null)
        {
            Button addVehicleButton = (Button)actionBar.getCustomView().findViewById(R.id.add_vehicle);

            addVehicleButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    View view = VehicleFragment.this.getView();

                    EditText regNoEditText = (EditText) view.findViewById(R.id.et_vehicle_regno);
                    EditText modelEditText = (EditText) view.findViewById(R.id.et_vehicle_model);
                    EditText phoneNoEditText = (EditText) view.findViewById(R.id.et_phoneno);
                    EditText serviceEditText = (EditText) view.findViewById(R.id.et_service);

                    if (validateInputs(regNoEditText.getText().toString().trim(), phoneNoEditText.getText().toString().trim()))
                    {
                        if (callbacks != null)
                        {
                            vehicle = new Vehicle();

                            vehicle.setRegNo(regNoEditText.getText().toString());
                            vehicle.setModel(modelEditText.getText().toString());
                            vehicle.setPhoneNo(phoneNoEditText.getText().toString());
                            vehicle.setService(serviceEditText.getText().toString());

                            callbacks.insertVehicle(vehicle);

                            hideVirtualKeyboard();

                            Toast.makeText(getActivity(), "Vehicle Added", Toast.LENGTH_SHORT).show();

                            registerVehicleDeviceID(vehicle.getRegNo(), SplashScreenActivity.deviceID);

                            submitSubsPolicyNotificationRequest(vehicle.getRegNo());

                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    callbacks.gotoServiceFragment(vehicle, true);
                                }
                            }, 1000);
                        }
                    }
                }
            });
        }
        else
        {
            Button editVehicleButton = (Button)actionBar.getCustomView().findViewById(R.id.edit_vehicle);

            editVehicleButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    View view = VehicleFragment.this.getView();

                    EditText regNoEditText = (EditText) view.findViewById(R.id.et_vehicle_regno);
                    EditText modelEditText = (EditText) view.findViewById(R.id.et_vehicle_model);
                    EditText phoneNoEditText = (EditText) view.findViewById(R.id.et_phoneno);
                    EditText serviceEditText = (EditText) view.findViewById(R.id.et_service);
                    if (validateInputs(regNoEditText.getText().toString().trim(), phoneNoEditText.getText().toString().trim()))
                    {
                        if (callbacks != null)
                        {
                            vehicle.setRegNo(regNoEditText.getText().toString());
                            vehicle.setModel(modelEditText.getText().toString());
                            vehicle.setPhoneNo(phoneNoEditText.getText().toString());
                            vehicle.setService(serviceEditText.getText().toString());

                            callbacks.updateVehicle(vehicle);

                            deleteVehicleButton.setVisibility(View.INVISIBLE);

                            hideVirtualKeyboard();

                            callbacks.gotoServiceFragment(vehicle, true);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        restoreActionBar();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            callbacks = (VehicleFragmentCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement VehicleFragmentCallbacks");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        callbacks = null;
    }

    private void registerVehicleDeviceID(String vehRegNo, String deviceID)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        String apiURL = CarfixAPIHelper.generateRegisterVehicleDeviceIDAPIURL(vehRegNo, deviceID);

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
    private void submitSubsPolicyNotificationRequest(String regNo)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final String apiURL = CarfixAPIHelper.generateSubsPolicyNotificationAPIURL(regNo);

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

    public interface VehicleFragmentCallbacks
    {
        public void insertVehicle(Vehicle vehicle);

        public void updateVehicle(Vehicle vehicle);

        public void deleteVehicle(Vehicle vehicle);

        public void gotoServiceFragment(Vehicle vehicle, boolean addToBackStack);

        public void gotoVehicleListFragment();
    }
}
