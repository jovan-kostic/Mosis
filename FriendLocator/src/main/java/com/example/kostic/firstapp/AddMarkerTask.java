package com.example.kostic.firstapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class AddMarkerTask extends AsyncTask<String, Void, String> {

    MainActivity ctx;
    Double longitude;
    Double latitude;
    String add_marker_url;
    Marker marker;
    MarkerOptions markerOptions;
    Circle circle;

    AddMarkerTask(MainActivity ctx) {
        this.ctx = ctx;
    }


    @Override
    protected void onPreExecute() {
        add_marker_url = "http://192.168.1.4/fl_server/add_marker.php";
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String add_marker_url = "http://192.168.1.4/fl_server/add_marker.php";
            try {
                Criteria criteria = new Criteria();
                String provider = ctx.locationManager.getBestProvider(criteria, true);
                Location location = ctx.locationManager.getLastKnownLocation(provider);
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                URL url = new URL(add_marker_url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(2000);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user", "UTF-8") +"="+URLEncoder.encode(ctx.username, "UTF-8")+"&"+
                        URLEncoder.encode("longitude", "UTF-8") +"="+URLEncoder.encode(longitude.toString(), "UTF-8")+"&"+
                        URLEncoder.encode("latitude", "UTF-8") +"="+URLEncoder.encode(latitude.toString(), "UTF-8")+"&"+
                        URLEncoder.encode("team", "UTF-8") +"="+URLEncoder.encode(ctx.team, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null)
                {
                    response = line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (SecurityException e) {
            }

            return "Connection error...";
      }

            @Override
            protected void onProgressUpdate(Void...values){
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result){
                    if (result.startsWith("Connection error...") || result.startsWith("Marker on this location already exists..."))
                    {
                        Toast toast = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
                        View toastView = toast.getView();
                        toastView.setBackgroundResource(R.drawable.toast);
                        toast.show();
                    }
                    else {

                        LatLng currentPosition = new LatLng(latitude, longitude);

                        if (ctx.team.equals("Green Team")) {
                            markerOptions = new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.green_flag))
                                    .position(currentPosition)
                                    .title(ctx.username);

                            marker = ctx.map.addMarker(markerOptions);

                            circle = ctx.map.addCircle(new CircleOptions()
                                    .center(currentPosition)
                                    .radius(300)
                                    .strokeColor(ctx.getResources().getColor(R.color.colorGreen))
                                    .fillColor(ctx.getResources().getColor(R.color.colorGreenTransparent)));
                        } else {

                            markerOptions = new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_flag))
                                    .position(currentPosition)
                                    .title(ctx.username);

                            marker = ctx.map.addMarker(markerOptions);

                            circle = ctx.map.addCircle(new CircleOptions()
                                    .center(currentPosition)
                                    .radius(300)
                                    .strokeColor(ctx.getResources().getColor(R.color.colorRed))
                                    .fillColor(ctx.getResources().getColor(R.color.colorRedTransparent)));
                        }

                        EventMarker eventMarker = new EventMarker(marker, circle, ctx.username, longitude, latitude,"");
                        ctx.treeMap.put(ctx.treeMap.size() + 1, eventMarker);

                        ctx.addMarkerDialog.dismiss();
                    }

                    ctx.pd.dismiss();
                }
}