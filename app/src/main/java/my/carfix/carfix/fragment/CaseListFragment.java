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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import my.carfix.adapter.CaseAdapter;
import my.carfix.carfix.model.LogCase;
import my.carfix.carfix.R;

public class CaseListFragment extends Fragment implements ListView.OnItemClickListener
{
    private List<LogCase> logCases;

    private ListView listView;

    private CaseListFragmentCallbacks callbacks;

    public CaseListFragment()
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

    public static CaseListFragment newInstance()
    {
        return new CaseListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_case_list, container, false);

        listView = (ListView)view.findViewById(R.id.list_view);

        logCases = callbacks.fetchLogCases();

        listView.setAdapter(new CaseAdapter(getActivity(), R.id.list_view, logCases));
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            callbacks = (CaseListFragmentCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement CaseListFragmentCallbacks");
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
            callbacks.gotoCaseFragment(logCases.get(position));
        }
    }

    public interface CaseListFragmentCallbacks
    {
        public List<LogCase> fetchLogCases();

        public void gotoCaseFragment(LogCase logCase);
    }
}
