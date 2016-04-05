package com.example.kostic.firstapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class RankActivity extends AppCompatActivity {
    ProgressDialog pd;
    String user;
    ListView listView;
    RankAdapter rankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rank);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = getIntent().getStringExtra("username");
        listView = (ListView)findViewById(R.id.listview);
        rankAdapter = new RankAdapter(this,R.layout.rank_table);
        listView.setAdapter(rankAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_username = (TextView)view.findViewById(R.id.username);
                String username = (String)tv_username.getText();
                Intent open_profile = new Intent();
                open_profile.setAction("com.example.kostic.firstapp.profile");
                open_profile.addCategory("android.intent.category.DEFAULT");
                open_profile.putExtra("username", username);
                startActivity(open_profile);
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Retrieving information...");
        pd.setCancelable(false);
        pd.show();
        RankTask rankTask= new RankTask(this,listView,rankAdapter);
        rankTask.execute();

    }


    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
