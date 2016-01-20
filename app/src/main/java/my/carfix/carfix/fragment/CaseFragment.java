package my.carfix.carfix.fragment;

import java.util.List;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import my.carfix.adapter.CaseMessageAdapter;

import my.carfix.carfix.R;
import my.carfix.carfix.model.LogCase;
import my.carfix.carfix.model.LogCaseMessage;

public class CaseFragment extends Fragment
{
    private LogCase logCase;

    private List<LogCaseMessage> logCaseMessages;

    private TextView textView;

    private ImageView imageView;

    private ListView listView;

    private CaseFragmentCallbacks callbacks;

    public CaseFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        TextView actionBarTitleTextView = (TextView)actionBar.getCustomView().findViewById(R.id.text_view_action_bar_title);
        actionBarTitleTextView.setText("Case History");

        super.onCreateOptionsMenu(menu, inflater);
    }

    public static CaseFragment newInstance(LogCase logCase)
    {
        CaseFragment fragment = new CaseFragment();

        fragment.logCase = logCase;

        return fragment;
    }

    private String eta(long arrivedTime)
    {
        if (arrivedTime == 0)
            return "-";
        else if (arrivedTime * 1000 < System.currentTimeMillis())
            return "0 Minutes";
        else
            return String.format("%d Minutes", (arrivedTime * 1000 - System.currentTimeMillis()) / 60000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_case, container, false);

        textView = (TextView)view.findViewById(R.id.text_view);
        textView.setHorizontallyScrolling(false);
        textView.setMaxLines(Integer.MAX_VALUE);

        textView.setText(String.format("Tow Truck : %s\nDriver's Name : %s\nETA : %s\nBreakdown Location : \n%s", logCase.getTruckNo(), logCase.getDriverName(), eta(logCase.getArrivedTime()), logCase.getLocation()));

        if (logCase.getImageURL().length() > 0)
        {
            imageView = (ImageView)view.findViewById(R.id.image_view);

            Picasso.with(getActivity()).load(logCase.getImageURL()).into(imageView);
        }

        listView = (ListView)view.findViewById(R.id.list_view);

        logCaseMessages = callbacks.fetchLogCaseMessages(logCase.getCaseNo());

        listView.setAdapter(new CaseMessageAdapter(getActivity(), R.id.list_view, logCaseMessages));

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            callbacks = (CaseFragmentCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement CaseFragmentCallbacks");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        callbacks = null;
    }

    public interface CaseFragmentCallbacks
    {
        public List<LogCaseMessage> fetchLogCaseMessages(String caseNo);
    }
}
