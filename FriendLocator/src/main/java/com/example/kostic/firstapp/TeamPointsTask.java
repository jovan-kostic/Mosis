package com.example.kostic.firstapp;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

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

public class TeamPointsTask extends AsyncTask<String, Void, String> {

    RankActivity ctx;
    String user;
    String json_url;
    String JSON_STRING;


    TeamPointsTask(RankActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {

        json_url = "http://192.168.1.4/fl_server/team_points.php";
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(json_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(5000);

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while ((JSON_STRING = bufferedReader.readLine()) != null) {
                stringBuilder.append(JSON_STRING + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
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
                    parseJSON(result);
                }
                ctx.pd.dismiss();
            }

         public void parseJSON(String result)
         {
             JSONObject jsonObject;
             JSONArray jsonArray;

             try {
                 jsonObject = new JSONObject(result);
                 jsonArray = jsonObject.getJSONArray("server_response");
                 jsonObject = jsonArray.getJSONObject(0);
                 ctx.tv_green_points.setText(jsonObject.getString("points"));
                 jsonObject = jsonArray.getJSONObject(1);
                 ctx.tv_red_points.setText(jsonObject.getString("points"));

             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }
}