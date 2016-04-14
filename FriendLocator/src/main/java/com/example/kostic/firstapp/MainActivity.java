package com.example.kostic.firstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;


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
    AlertDialog searchDialog,friendDialog;
    String username, team, radio_team, marker_team;
    int updater;
    boolean add=true;
    boolean capture= false;
    ListView searchListView,bluetoothListView;
    SearchAdapter searchAdapter;
    EditText et_distance;

    int REQUEST_ENABLE_BT;
    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver receiver;
    protected static Handler mHandler;
    protected static final int SUCCESS_CONNECT=0;
    protected static final int MESSAGE_READ=1;
    public static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


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

        //BLUETOOTH HANDLER

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler() {
        public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case SUCCESS_CONNECT:
                        ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
                        String s_write = "success!";
                        toast = Toast.makeText(MainActivity.this, "Connection success!", Toast.LENGTH_LONG);
                        View toastView = toast.getView();
                        toastView.setBackgroundResource(R.drawable.toast);
                        toast.show();
                        connectedThread.write(s_write.getBytes());
                        break;
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[])msg.obj;
                        String s_read = new String (readBuf);
                        break;
                }
            }
        };

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
                                    toast = Toast.makeText(MainActivity.this, "Enemy players: " + players2 + "\nvs\nFriendly players: " + players1, Toast.LENGTH_LONG);
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
                open_profile.putExtra("user", username);
                startActivity(open_profile);
                return true;
            case R.id.action_rank:
                Intent open_rank = new Intent();
                open_rank.setAction("com.example.kostic.firstapp.Rank");
                open_rank.addCategory("android.intent.category.DEFAULT");
                open_rank.putExtra("user", username);
                startActivity(open_rank);
                return true;
           case R.id.search:
                radio_team = "Both";
                pd = new ProgressDialog(MainActivity.this);
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
                        EventMarker marker = (EventMarker) parent.getItemAtPosition(position);
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
                    return true;

                case R.id.add_friend:
                if (!bluetoothAdapter.isEnabled())
                {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
                } else {
                    Bluetooth();
                }

                return true;
                     default:
                             return super.onOptionsItemSelected(item);
                 }

            }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivity(discoverableIntent);
            Bluetooth();

        }
    }

    public void Bluetooth()
    {
        LayoutInflater inflater = getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.add_friend, null);
        friendDialog = new AlertDialog.Builder(MainActivity.this).create();
        friendDialog.setView(dialoglayout);
        friendDialog.show();

        bluetoothListView = (ListView) dialoglayout.findViewById(R.id.bluetoothListView);
        final ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
        bluetoothListView.setAdapter(deviceAdapter);
        final Set<BluetoothDevice> devicesArray = bluetoothAdapter.getBondedDevices();
        final ArrayList<String> pairedDeviceStrings = new ArrayList<String>();
        final ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(bluetoothAdapter.isDiscovering())
                {
                    bluetoothAdapter.cancelDiscovery();
                }

                if(deviceAdapter.getItem(position).contains("Paired"))
                {
                    BluetoothDevice selectedDevice = devices.get(position);
                    ConnectThread connect = new ConnectThread(selectedDevice);
                    connect.start();
                }
                else {
                    toast = Toast.makeText(MainActivity.this, "Devices must be paired...", Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast);
                    toast.show();
                }
            }
        });


        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray) {
                pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
            }
        }

        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s="";
                    for(int a=0; a < pairedDeviceStrings.size();a++)
                    {
                        if (pairedDeviceStrings.get(a).startsWith(device.getName())){
                            s = "(Paired)";
                        }
                    }

                    deviceAdapter.add(device.getName()+ " " + s + "\n" +device.getAddress());

                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){

                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                    if(!deviceAdapter.isEmpty())
                    {
                        for(int i=0; i < deviceAdapter.getCount();i++)
                        {
                            for(int a=0; a < pairedDeviceStrings.size();a++)
                            {
                                if (deviceAdapter.getItem(i).equals(pairedDeviceStrings.get(a))){
                                    //apend

                                    break;
                                }
                            }
                        }
                    }
                }
                else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                        if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF){
                            //Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            //startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
                            friendDialog.dismiss();
                        }
                }
            }
        };
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    private class ConnectThread extends Thread {

        private  BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exceptions
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.e("TAG", connectException.getMessage());
                try {

                    mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,2);
                    mmSocket.connect();

                } catch (Exception e2) {

                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        Log.e("TAG", connectException.getMessage());
                    }
                    return;
                }
            }

            // Do work to manage the connection (in a separate thread)
            mHandler.obtainMessage(SUCCESS_CONNECT,mmSocket).sendToTarget();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    buffer = new byte[1024];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
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
            if (receiver!=null){unregisterReceiver(receiver);}
            super.finish();
        }
    }


}
