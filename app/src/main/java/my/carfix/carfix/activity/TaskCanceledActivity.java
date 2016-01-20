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

import my.carfix.carfix.R;

public class TaskCanceledActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_canceled);

        TextView detail = (TextView)findViewById(R.id.text_view);

        Intent intent = getIntent();

        String userCode = intent.getStringExtra("userCode");
        String key = intent.getStringExtra("key");
        String truckNo = intent.getStringExtra("truckNo");
        String caseNo = intent.getStringExtra("caseNo");
        String vehRegNo = intent.getStringExtra("vehRegNo");

        detail.setText(String.format("Job Canceled\n\nCase No : %s\nVehicle Reg. No. : %s\n\nTask has been canceled, last job info have been clear.",
                caseNo,
                vehRegNo));

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
        getMenuInflater().inflate(R.menu.menu_task_canceled, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }
}
