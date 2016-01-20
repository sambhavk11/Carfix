package my.carfix.carfix.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import my.carfix.carfix.R;

public class AboutUsFragment extends WebViewFragment
{
    public static AboutUsFragment newInstance()
    {
        Bundle args = new Bundle();
        args.putString("infoURL", "http://carfix.my/Other/About?Header=0");

        AboutUsFragment fragment = new AboutUsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public AboutUsFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        TextView actionBarTitleTextView = (TextView)actionBar.getCustomView().findViewById(R.id.text_view_action_bar_title);
        actionBarTitleTextView.setText("About Us");

        super.onCreateOptionsMenu(menu, inflater);
    }
}
