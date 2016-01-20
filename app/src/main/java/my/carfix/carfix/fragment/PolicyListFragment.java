package my.carfix.carfix.fragment;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import my.carfix.adapter.PoliciesAdapter;
import my.carfix.carfix.R;
import my.carfix.carfix.activity.MainDrawerActivity;
import my.carfix.datastructure.CarFixData;
import my.carfix.datastructure.PoliciesData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PolicyListFragment extends Fragment
{
    private static final String type1 = "Please choose service that you want";

    private static final String type2 = "Request successful but policy not found. Please contact your respected insurance's personnel to proceed. \n";

    private PolicyListFragmentCallbacks callbacks;

    private TextView policyInstruction;

    private ListView listView;

	private PoliciesAdapter adapter;

	private CarFixData carFixData;

	private int policyType;

    private CarFixData newCarFixData;

	private ProgressDialog mProgress;

	private String mImage;

	private String mName;

	private String mHotline;

    public static PolicyListFragment newInstance(CarFixData carFixData, int policyType)
    {
        Bundle args = new Bundle();
        args.putParcelable("CarFixData", carFixData);
        args.putInt("policyType", policyType);

        PolicyListFragment fragment = new PolicyListFragment();
        fragment.setArguments(args);

        return fragment;
    }

	@Override
	public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		View view = inflater.inflate(R.layout.fragment_policies, container, false);

		policyInstruction  = (TextView) view.findViewById(R.id.policy_instruction);
		listView = (ListView) view.findViewById(R.id.policies_list);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
    {
		super.onActivityCreated(savedInstanceState);

		Bundle args = getArguments();
		carFixData = args.getParcelable("CarFixData");
		policyType = args.getInt("policyType");

		if(policyType == 1)
        {
			policyInstruction.setText(type1);

            adapter = new PoliciesAdapter(getActivity(), R.id.policies_list, carFixData.MatchedPolicies, policyType);
		}
        else if (policyType == 2)
        {
			policyInstruction.setText(Html.fromHtml(type2 + "Your passcode is <b>" + carFixData.Passcode + "</b>"));

            adapter = new PoliciesAdapter(getActivity(), R.id.policies_list, carFixData.GeneralHelps, policyType);
		}

		listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
				PoliciesData policiesData = (PoliciesData) parent.getAdapter().getItem(position);
				if(policyType == 1)
                {
					submitPolicyConfirmation("Confirm", "You've selected the policy by " + policiesData.SubscriberName, policiesData.PolicyID, policiesData.Logo, policiesData.SubscriberName, policiesData.Hotline);
				}
                else if(policyType == 2)
                {
					dialogPhoneCallConfirmation("Confirm to call?", policiesData.SubscriberName + ":\n" + policiesData.Hotline, policiesData.Hotline);
				}
			}
		});
	}


	public void dialogPhoneCallConfirmation(String title, String message, final String phoneNumber)
    {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		mBuilder.setTitle(title);
		mBuilder.setMessage(message);
		mBuilder.setPositiveButton("call", new DialogInterface.OnClickListener()
        {
			public void onClick(DialogInterface dialog, int id)
            {
				phoneDial(phoneNumber);
				dialog.dismiss();

			}
		});
		mBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
            {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = mBuilder.create();
		dialog.show();
	}

	public void submitPolicyConfirmation(String title, String message, final int policyID, String policyImage, String policyName, String policyHotline)
    {
		mImage = policyImage;
		mName = policyName;
		mHotline = policyHotline;
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		mBuilder.setTitle(title);
		mBuilder.setMessage(message);
		mBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
			public void onClick(DialogInterface dialog, int id)
            {
				dialog.dismiss();
				submitSelectedPolicy(policyID);
			}
		});
		mBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
            {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = mBuilder.create();
		dialog.show();
	}

	public void phoneDial(String phoneNumber)
    {
        String url = "tel:" + phoneNumber;

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));

        startActivity(intent);
	}

	public void submitSelectedPolicy(int policyID)
    {
		AsyncHttpClient mAsyncClient = new AsyncHttpClient();

        String apiURL = String.format(MainDrawerActivity.DEVELOPMENT_API_URL2,
                carFixData.VehReg, carFixData.PhoneNo, carFixData.Address,
                carFixData.Latitude, carFixData.Longitude, carFixData.VehModel, carFixData.ServiceNeeded, policyID);

        mAsyncClient.get(getActivity(), apiURL, new AsyncHttpResponseHandler()
        {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
				String response = new String(arg2);
				newCarFixData = CarFixData.get(response);
				if (newCarFixData != null && callbacks != null)
                {
                    callbacks.gotoPolicyFragment(mImage, mName, mHotline, carFixData.PhoneNo, carFixData.Passcode);
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

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            callbacks = (PolicyListFragmentCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement PolicyListFragmentCallbacks");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        callbacks = null;
    }

    public interface PolicyListFragmentCallbacks
    {
        public void gotoPolicyFragment(String policyImage, String policyName, String policyHotline, String userPhoneNo, String passcode);
    }
}
