package com.example.kostic.firstapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;


public class ProfileActivity extends AppCompatActivity {

    String user;
    TextView fname,lname,phone,username,password;;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fname = (TextView)findViewById(R.id.firstname);
        lname = (TextView)findViewById(R.id.lastname);
        phone = (TextView)findViewById(R.id.phone);
        username = (TextView)findViewById(R.id.username);
        password = (TextView)findViewById(R.id.password);

        user = getIntent().getStringExtra("username");

        pd = new ProgressDialog(this);
        pd.setMessage("Retrieving information...");
        pd.setCancelable(false);
        pd.show();
        ProfileTask profileTask = new ProfileTask(this);
        profileTask.execute(user);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
           /* Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);*/
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
