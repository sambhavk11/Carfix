package my.carfix.carfix.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import my.carfix.carfix.R;

public class WebViewFragment extends Fragment
{
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		View view = inflater.inflate(R.layout.fragment_web_view, container, false);

		webView = (WebView) view.findViewById(R.id.web_view);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
    {
		super.onActivityCreated(savedInstanceState);
		ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
	    actionBar.setDisplayShowHomeEnabled(false);
	    actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.action_bar_main);
        View customView = actionBar.getCustomView();
        Toolbar parent =(Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

		Bundle args = getArguments();
             String url = args.getString("infoURL");

             final Activity activity = getActivity();
             webView.setWebChromeClient(new WebChromeClient()
             {
                 public void onProgressChanged(WebView view, int progress)
                 {
                 }
             });
        webView.setWebViewClient(new WebViewClient() {
		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		     Toast.makeText(activity, "Oops! " + description, Toast.LENGTH_SHORT).show();
		   }

		  @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
				//return super.shouldOverrideUrlLoading(view, url);
			}
		 });

		webView.loadUrl(url);
	}
}
