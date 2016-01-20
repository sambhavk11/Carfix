package my.carfix.carfix.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import my.carfix.carfix.R;

public class ContactUsFragment extends WebViewFragment
{
    public static ContactUsFragment newInstance()
    {
        Bundle args = new Bundle();
        args.putString("infoURL", "http://carfix.my/Other/Contact?Header=0");

        ContactUsFragment fragment = new ContactUsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public ContactUsFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        TextView actionBarTitleTextView = (TextView)actionBar.getCustomView().findViewById(R.id.text_view_action_bar_title);
        actionBarTitleTextView.setText("Contact Us");

        super.onCreateOptionsMenu(menu, inflater);
    }
}
