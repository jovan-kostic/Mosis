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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
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

import java.util.ArrayList;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap map;
    Double city_longitude, city_latitude, marker_longitude, marker_latitude, user_longitude, user_latitude, capture_marker_latitude, capture_marker_longitude;
    int city_radius, marker_radius,players1,players2,greens,reds,captureMarkerPosition,add_flag_pts,capture_flag_pts;
    Circle cityCircle;
    CircleOptions cityCircleOptions;
    LocationManager locationManager;
    TreeMap<Integer, EventMarker> markerMap = new TreeMap<Integer,EventMarker>();
    TreeMap<Integer, Player> playerMap = new TreeMap<Integer,Player>();
    Marker marker;
    ProgressDialog pd;
    AlertDialog addMarkerDialog;
    AlertDialog searchDialog;
    String username, team, radio_team, marker_team;
    int updater;
    boolean add=true;
    boolean capture= false;
    ListView searchListView;
    SearchAdapter searchAdapter;
    EditText et_distance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username");
        team = getIntent().getStringExtra("team");

        updater = 0;
        add_flag_pts = 10;
        capture_flag_pts = 20;
        greens = 0;
        reds = 0;
        players1 = 1;
        players2 = 0;
        city_longitude = 21.8967985; // Nis location
        city_latitude = 43.31914696;
        city_radius = 1800;
        marker_radius = 300;
        radio_team="Both";

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

                addMarkerDialog = new AlertDialog.Builder(MainActivity.this).create();
                addMarkerDialog.setView(dialoglayout);
                addMarkerDialog.show();

                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Updating flags...");
                pd.setCancelable(false);
                pd.show();
                GetMarkerTask getMarkerTask = new GetMarkerTask(MainActivity.this);
                getMarkerTask.execute();
                GetPlayerTask getPlayerTask = new GetPlayerTask(MainActivity.this);
                getPlayerTask.execute();

                final Button addMarkerButton = (Button) addMarkerDialog.findViewById(R.id.add_marker);
                addMarkerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage("Capturing...");
                        pd.setCancelable(false);
                        pd.show();

                        //GAME LOGIC
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
                            pd.dismiss();
                            toast.show();
                            return;
                        }

                        //add/capture marker on free location
                        try {

                            Location location = locationManager.getLastKnownLocation(provider);
                            user_latitude = location.getLatitude();
                            user_longitude = location.getLongitude();
                            for (int i=1;i<markerMap.size()+1;i++)
                            {
                                marker_latitude = markerMap.get(i).getLatitude();
                                marker_longitude = markerMap.get(i).getLongitude();
                                Location.distanceBetween(user_latitude, user_longitude, marker_latitude, marker_longitude, distance);
                                if (distance[0] < marker_radius*2 && distance[0] > marker_radius)
                                {add=false;}
                                if (distance[0] < marker_radius)
                                {
                                    add = false;
                                    if(!markerMap.get(i).getTeam().equals(team)) {
                                        capture = true;
                                        capture_marker_latitude = marker_latitude;
                                        capture_marker_longitude = marker_longitude;
                                        captureMarkerPosition = i;
                                    }
                                }
                            }
                            if (add) {
                                toast = Toast.makeText(MainActivity.this, "Terittory captured!\nYou gain +10 points", Toast.LENGTH_LONG);
                                View toastView = toast.getView();
                                toastView.setBackgroundResource(R.drawable.toast);
                                toast.show();
                                AddMarkerTask addMarkerTask = new AddMarkerTask(MainActivity.this);
                                addMarkerTask.execute();
                                PointTask pointTask = new PointTask(MainActivity.this);
                                pointTask.execute(add_flag_pts);

                            } else if(capture) {
                                for (int i=1;i<playerMap.size()+1;i++)
                                {
                                    marker_latitude = playerMap.get(i).getLatitude();
                                    marker_longitude = playerMap.get(i).getLongitude();
                                    Location.distanceBetween(capture_marker_latitude, capture_marker_longitude, marker_latitude, marker_longitude, distance);
                                    if (distance[0] < marker_radius)
                                    {
                                        if (playerMap.get(i).getTeam().equals(team)) {
                                            players1++;
                                        }
                                        else
                                        {
                                            players2++;
                                        }
                                    }

                                }
                                if (players1>players2) {
                                    toast = Toast.makeText(MainActivity.this, "Enemy territory captured!\nYou gain +20 points", Toast.LENGTH_LONG);
                                    View toastView = toast.getView();
                                    toastView.setBackgroundResource(R.drawable.toast);
                                    toast.show();
                                    UpdateMarkerTask updateMarkerTask = new UpdateMarkerTask(MainActivity.this);
                                    updateMarkerTask.execute();
                                    PointTask pointTask = new PointTask(MainActivity.this);
                                    pointTask.execute(capture_flag_pts);
                                }
                                else {
                                    toast = Toast.makeText(MainActivity.this, "\nEnemy players: " + players2 + "\nvs\nFriendly players: " + players1, Toast.LENGTH_LONG);
                                    View toastView = toast.getView();
                                    toastView.setBackgroundResource(R.drawable.toast);
                                    toast.show();
                                }
                            }

                            else {
                                toast = Toast.makeText(MainActivity.this, "Other flag is too close!", Toast.LENGTH_LONG);
                                View toastView = toast.getView();
                                toastView.setBackgroundResource(R.drawable.toast);
                                toast.show();
                            }
                        } catch (SecurityException e) {}

                        players1=1;
                        players2=0;
                        add = true;
                        capture = false;
                        pd.dismiss();
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
            //ADD MARKER ON CURRENT LOCATION
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

            //DRAW CITY CIRCLE
            LatLng cityPosition = new LatLng(city_latitude, city_longitude);
            cityCircleOptions = new CircleOptions().center(cityPosition)
                    .radius(city_radius)
                    .strokeColor(getResources().getColor(R.color.colorGray))
                    .fillColor(getResources().getColor(R.color.colorGrayTransparent));
            cityCircle = map.addCircle(cityCircleOptions);

            //GETTING USERS
            GetPlayerTask getPlayerTask = new GetPlayerTask(MainActivity.this);
            getPlayerTask.execute();

            //GETTING MARKERS
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
            updater++;
            if (updater == 1)
            {
                //update markers and players
                GetPlayerTask getPlayerTask = new GetPlayerTask(MainActivity.this);
                getPlayerTask.execute();
                GetMarkerTask getMarkerTask = new GetMarkerTask(MainActivity.this);
                getMarkerTask.execute();
                updater = 0;
            }
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
            case R.id.search:
            {
                radio_team = "Both";
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Updating flags...");
                pd.setCancelable(false);
                pd.show();
                GetMarkerTask getMarkerTask = new GetMarkerTask(MainActivity.this);
                getMarkerTask.execute();

                LayoutInflater inflater = getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.search, null);
                searchDialog = new AlertDialog.Builder(MainActivity.this).create();
                searchDialog.setView(dialoglayout);
                searchDialog.show();

                searchListView = (ListView) dialoglayout.findViewById(R.id.searchListView);
                searchAdapter = new SearchAdapter(this,R.layout.search_table);
                searchListView.setAdapter(searchAdapter);

                searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        EventMarker marker = (EventMarker)parent.getItemAtPosition(position);
                        LatLng markerPosition = new LatLng(marker.getLatitude(), marker.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 16));
                        searchDialog.dismiss();
                    }
                });

                         Button searchButton = (Button) searchDialog.findViewById(R.id.search);
                         searchButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 try {

                                     searchAdapter.list.clear();
                                     searchAdapter.notifyDataSetChanged();
                                     Criteria criteria = new Criteria();
                                     String provider = locationManager.getBestProvider(criteria, true);
                                     float[] distance = new float[1];
                                     float dist;
                                     Location location = locationManager.getLastKnownLocation(provider);
                                     user_latitude = location.getLatitude();
                                     user_longitude = location.getLongitude();
                                     et_distance = (EditText) dialoglayout.findViewById(R.id.e1);

                                     if (et_distance.getText().toString().equals("")) {
                                         dist = city_radius;
                                     } else {
                                         dist = Float.parseFloat(et_distance.getText().toString());
                                     }
                                     for (int i = 1; i < markerMap.size() + 1; i++) {
                                         marker_latitude = markerMap.get(i).getLatitude();
                                         marker_longitude = markerMap.get(i).getLongitude();
                                         marker_team = markerMap.get(i).getTeam();
                                         Location.distanceBetween(user_latitude, user_longitude, marker_latitude, marker_longitude, distance);
                                         if (radio_team.equals("Both")) {
                                             if (distance[0] < dist) {
                                                 markerMap.get(i).setInfo(String.valueOf(Math.round(distance[0])) + "m");
                                                 searchAdapter.add(markerMap.get(i));
                                             }
                                         } else if (distance[0] < dist && marker_team.equals(radio_team)) {
                                             markerMap.get(i).setInfo(String.valueOf(Math.round(distance[0])) + "m");
                                             searchAdapter.add(markerMap.get(i));
                                         }
                                     }
                                     if (searchAdapter.list.isEmpty()) {
                                         toast = Toast.makeText(MainActivity.this, "No results for selected criteria...", Toast.LENGTH_LONG);
                                         View toastView = toast.getView();
                                         toastView.setBackgroundResource(R.drawable.toast);
                                         toast.show();
                                     }
                                 } catch (SecurityException e) {
                                 }

                             }
                         });

                         Button cancelButton = (Button) searchDialog.findViewById(R.id.cancel);
                         cancelButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 searchDialog.dismiss();
                             }
                         });

                     }

                     default:
                             return super.

                     onOptionsItemSelected(item);
                 }

            }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.both:
                if (checked)
                    radio_team = "Both";
                break;
            case R.id.green:
                if (checked)
                    radio_team = "Green Team";
                break;
            case R.id.red:
                if (checked)
                    radio_team = "Red Team";
                break;
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
