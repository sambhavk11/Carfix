package my.carfix.carfix.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import my.carfix.carfix.R;
import my.carfix.carfix.activity.MainDrawerActivity;
import my.carfix.carfix.activity.SplashScreenActivity;
import my.carfix.datastructure.CarFixData;

public class PolicyFragment extends Fragment
{
    public static final String PRODUCTION_API_URL = "";

    public static final String DEVELOPMENT_API_URL = "http://54.179.190.147:8080/CarfixWebService/RegisterCaseDeviceID?CaseNo=%s&DeviceID=%s";

    public ImageView policyImage;
	public TextView policyName;
	public TextView policyHotline;
	public TextView instruction;

    public static PolicyFragment newInstance(String policyImage, String policyName, String policyHotline, String userPhoneNo, String passcode)
    {
        Bundle args = new Bundle();
        args.putString("policyImage", policyImage);
        args.putString("policyName", policyName);
        args.putString("policyHotline", policyHotline);
        args.putString("userPhoneNo", userPhoneNo);
        args.putString("passcode", passcode);

        PolicyFragment fragment = new PolicyFragment();
        fragment.setArguments(args);

        return fragment;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		View view = inflater.inflate(R.layout.fragment_confirm_policy, container, false);

        policyImage = (ImageView) view.findViewById(R.id.policy_image);
        policyName = (TextView) view.findViewById(R.id.policy_name);
        policyHotline = (TextView) view.findViewById(R.id.policy_hotline);
        instruction = (TextView) view.findViewById(R.id.instruction);

        return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
    {
		super.onActivityCreated(savedInstanceState);

		Bundle args = getArguments();
		String mImage = args.getString("policyImage");
		String mName = args.getString("policyName");
		String mHotline = args.getString("policyHotline");
		String userPhoneNo = args.getString("userPhoneNo");
        String passcode = args.getString("passcode");

        registerCaseDeviceID(passcode);

        if (mImage != null)
        {
            Picasso.with(getActivity()).load(mImage).into(policyImage);
        }

		policyName.setText(mName);
		policyHotline.setText("Hotline: " + mHotline);
		instruction.setText("You have successfully submitted your query and our operator will contact you at "+ userPhoneNo + " shortly ");


    }

    private void registerCaseDeviceID(String passcode)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        String apiURL = String.format(DEVELOPMENT_API_URL, passcode, SplashScreenActivity.deviceID);

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
}
