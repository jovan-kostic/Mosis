package com.example.kostic.firstapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Circle;

import java.util.TreeMap;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap map;
    Double city_longitude;
    Double city_latitude;
    int city_radius;
    LocationManager locationManager;
    TreeMap<Integer, EventMarker> treeMap = new TreeMap<Integer,EventMarker>();
    Marker marker;
    ProgressDialog pd;
    AlertDialog addMarkerDialog;
    String username;
    String team;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username");
        team = getIntent().getStringExtra("team");

        city_longitude = 21.8967985; // Nis location
        city_latitude = 43.31914696;
        city_radius = 1800;

        //ADDING MARKER

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (team.equals("Red Team")){

            fab.setImageDrawable(getResources().getDrawable(R.mipmap.red_flag));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.add_marker, null);


                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                float[] distance = new float[1];
                try {

                    Location location = locationManager.getLastKnownLocation(provider);
                    Location.distanceBetween(location.getLatitude(),location.getLongitude(),city_latitude,city_longitude,distance);
                } catch (SecurityException e) {  }


                if (distance[0] > city_radius)
                {
                    toast = Toast.makeText(MainActivity.this, "You are out of battlefield boundaries", Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast);
                    toast.show();
                    return;
                }

                addMarkerDialog = new AlertDialog.Builder(MainActivity.this).create();
                addMarkerDialog.setView(dialoglayout);
                addMarkerDialog.show();
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Updating flags...");
                pd.setCancelable(false);
                pd.show();
                GetMarkerTask getMarkerTask = new GetMarkerTask(MainActivity.this);
                getMarkerTask.execute();

                final Button addMarkerButton = (Button) addMarkerDialog.findViewById(R.id.add_marker);
                addMarkerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage("Capturing...");
                        pd.setCancelable(false);
                        pd.show();
                        AddMarkerTask addMarkerTask = new AddMarkerTask(MainActivity.this);
                        addMarkerTask.execute();
                    }
                });

                Button cancelButton = (Button) addMarkerDialog.findViewById(R.id.cancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addMarkerDialog.dismiss();
                    }
                });

            }
        });


        //LOCATION AND MAP SERVICES

        // Get the LocationManager object from the System Service LOCATION_SERVICE
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        // Create a criteria object needed to retrieve the provider
        Criteria criteria = new Criteria();

        // Get the name of the best available provider
        final String provider = locationManager.getBestProvider(criteria, true);

        // We can use the provider immediately to get the last known location
        try { Location location = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {  }

        // request that the provider send this activity GPS updates every 20 seconds
        try { locationManager.requestLocationUpdates(provider, 20000, 0, this);
        } catch (SecurityException e) {  }

        //getting map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

   }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentPosition)
                    .title(username);

            marker = map.addMarker(markerOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
            map.getUiSettings().setMapToolbarEnabled(false);

            //draw circle - city boundaries
            LatLng cityPosition = new LatLng(city_latitude, city_longitude);
            Circle circle = map.addCircle(new CircleOptions()
                    .center(cityPosition)
                    .radius(city_radius)
                    .strokeColor(getResources().getColor(R.color.colorGray))
                    .fillColor(getResources().getColor(R.color.colorGrayTransparent)));

            //GETTING MARKERS INITIAL

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Map preparing...");
            pd.setCancelable(false);
            pd.show();
            GetMarkerTask getMarkerTask = new GetMarkerTask(MainActivity.this);
            getMarkerTask.execute();


        } catch (SecurityException e) {}
    }

    @Override
    public void onLocationChanged(Location location) {
        if (map != null)
        {
            drawMarker(location);
        }
    }

    private void drawMarker(Location location){
        if (marker!=null)
        {marker.remove();}

        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        LocationUpdateTask locationUpdateTask = new LocationUpdateTask(MainActivity.this);
        locationUpdateTask.execute(location.getLatitude(),location.getLongitude());

       //update current location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,16));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentPosition)
                .title(username);

        marker = map.addMarker(markerOptions);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent open_profile = new Intent();
                open_profile.setAction("com.example.kostic.firstapp.profile");
                open_profile.addCategory("android.intent.category.DEFAULT");
                open_profile.putExtra("username", username);
                startActivity(open_profile);
                return true;
            case R.id.action_rank:
                Intent open_rank = new Intent();
                open_rank.setAction("com.example.kostic.firstapp.Rank");
                open_rank.addCategory("android.intent.category.DEFAULT");
                open_rank.putExtra("username", username);
                startActivity(open_rank);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {

            toast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.toast);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            try {
            locationManager.removeUpdates(this);} catch (SecurityException e) {  }
            super.finish();
        }
    }


}
