package my.carfix.carfix.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import my.carfix.carfix.R;

public class PolicyExpireActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_expire);

        TextView detail = (TextView)findViewById(R.id.text_view);

        Intent intent = getIntent();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String vehRegNo = intent.getExtras().getString("vehRegNo");
        Date policyEffectiveDate = new Date(intent.getExtras().getLong("policyEffective", 0) * 1000);
        Date policyExpiredDate = new Date(intent.getExtras().getLong("policyExpired", 0) * 1000);
        String message = intent.getExtras().getString("message");

        detail.setText(String.format("Policy Reminder\n\nVehicle Reg. No : %s\nPolicy Effective Date : %s\nPolicy Expire Date : %s\n\nMessage : \n\n%s",
                vehRegNo,
                sdf.format(policyEffectiveDate),
                sdf.format(policyExpiredDate),
                message));

        Button backButton = (Button)findViewById(R.id.back);

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.action_bar_main);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDefaultDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_policy_expire, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }
}
