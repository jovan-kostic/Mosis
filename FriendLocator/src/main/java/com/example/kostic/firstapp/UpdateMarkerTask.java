package com.example.kostic.firstapp;

import android.location.Criteria;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class UpdateMarkerTask extends AsyncTask<String, Void, String> {

    MainActivity ctx;
    String update_marker_url;

    UpdateMarkerTask(MainActivity ctx) {
        this.ctx = ctx;
    }


    @Override
    protected void onPreExecute() {
        update_marker_url = "http://192.168.1.4/fl_server/update_marker.php";
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

            try {

                URL url = new URL(update_marker_url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(2000);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user", "UTF-8") +"="+URLEncoder.encode(ctx.username, "UTF-8")+"&"+
                        URLEncoder.encode("longitude", "UTF-8") +"="+URLEncoder.encode(ctx.capture_marker_longitude.toString(), "UTF-8")+"&"+
                        URLEncoder.encode("latitude", "UTF-8") +"="+URLEncoder.encode(ctx.capture_marker_latitude.toString(), "UTF-8")+"&"+
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
                    if (result.startsWith("Connection error..."))
                    {
                        Toast toast = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
                        View toastView = toast.getView();
                        toastView.setBackgroundResource(R.drawable.toast);
                        toast.show();
                    }
                    else {
                        int strokeColor;
                        int fillColor;

                       if (ctx.team.equals("Green Team")) {
                            ctx.greens++;
                             strokeColor = ctx.getResources().getColor(R.color.colorGreen);
                             fillColor = ctx.getResources().getColor(R.color.colorGreenTransparent);
                        } else {
                           ctx.reds++;
                            strokeColor = ctx.getResources().getColor(R.color.colorRed);
                            fillColor = ctx.getResources().getColor(R.color.colorRedTransparent);
                        }
                       ctx.markerMap.get(ctx.captureMarkerPosition).getMarker().setTitle(ctx.username);
                       ctx.markerMap.get(ctx.captureMarkerPosition).getCircle().setStrokeColor(strokeColor);
                       ctx.markerMap.get(ctx.captureMarkerPosition).getCircle().setFillColor(fillColor);

                        //draw city circle
                        if (ctx.greens < ctx.greens)
                        {
                            strokeColor = ctx.getResources().getColor(R.color.colorRedCityCircle);
                            fillColor = ctx.getResources().getColor(R.color.colorRedTransparentCityCircle);
                        }
                        else if (ctx.greens > ctx.reds)
                        {
                            strokeColor = ctx.getResources().getColor(R.color.colorGreenCityCircle);
                            fillColor = ctx.getResources().getColor(R.color.colorGreenTransparentCityCircle);
                        }
                        else
                        {
                            strokeColor = ctx.getResources().getColor(R.color.colorGray);
                            fillColor = ctx.getResources().getColor(R.color.colorGrayTransparent);
                        }
                        ctx.cityCircle.setStrokeColor(strokeColor);
                        ctx.cityCircle.setFillColor(fillColor);

                        ctx.addMarkerDialog.dismiss();
                    }

                    ctx.pd.dismiss();
                }
}