package com.example.kostic.firstapp;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class GetPlayerTask extends AsyncTask<Void, Void, String> {

    MainActivity ctx;
    String json_url;
    String JSON_STRING;
    Marker marker;
    MarkerOptions markerOptions;


    GetPlayerTask(MainActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {

        json_url = "http://192.168.1.4/fl_server/player.php";
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL(json_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(2000);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(ctx.username, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while ((JSON_STRING = bufferedReader.readLine()) != null) {
                stringBuilder.append(JSON_STRING + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            connection.disconnect();
            return stringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                    ctx.finish();
                }
                else
                {
                    if (!ctx.playerMap.isEmpty())
                    {
                        for (int i=1;i<ctx.playerMap.size()+1;i++)
                        {
                            if (ctx.playerMap.get(i).getTeam().equals(ctx.team))
                            {
                                ctx.playerMap.get(i).getMarker().remove();
                            }
                        }
                        ctx.playerMap.clear();
                    }
                    parseJSON(result);
                }
                ctx.pd.dismiss();
            }

         public void parseJSON(String result)
         {
             JSONObject jsonObject;
             JSONArray jsonArray;
             int count = 0;
             String username,team;
             Double longitude,latitude;
             Integer rank;

             try {

                 jsonObject = new JSONObject(result);
                 jsonArray = jsonObject.getJSONArray("server_response");

                 while(count<jsonArray.length())
                 {
                     jsonObject = jsonArray.getJSONObject(count);
                     username = jsonObject.getString("username");
                     longitude = Double.parseDouble((jsonObject.getString("longitude")));
                     latitude = Double.parseDouble((jsonObject.getString("latitude")));
                     team = jsonObject.getString("team");
                     rank = Integer.parseInt(jsonObject.getString("rank"));

                     LatLng markerPosition = new LatLng(latitude, longitude);

                     if (team.equals(ctx.team))
                     {
                         markerOptions = new MarkerOptions()
                                 .position(markerPosition)
                                 .title(username);
                         marker = ctx.map.addMarker(markerOptions);
                     }

                     Player player = new Player(marker,username,longitude,latitude,team,rank);
                     ctx.playerMap.put(ctx.playerMap.size() + 1, player);

                     count++;
                 }
            } catch (JSONException e) {
                 e.printStackTrace();
             }
         }
}