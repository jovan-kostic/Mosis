package com.example.kostic.firstapp;

import android.os.AsyncTask;

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

public class PointTask extends AsyncTask<Integer, Void, String> {

    MainActivity ctx;
    int point;
    String point_update_url;

    PointTask(MainActivity ctx) {
        this.ctx = ctx;
    }


    @Override
    protected void onPreExecute() {
        point_update_url = "http://192.168.1.4/fl_server/point_update.php";
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... params) {

        point = params[0];

            try {
                URL url = new URL(point_update_url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(2000);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user", "UTF-8") +"="+URLEncoder.encode(ctx.username, "UTF-8")+"&"+
                        URLEncoder.encode("point", "UTF-8") +"="+URLEncoder.encode(String.valueOf(point), "UTF-8");
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
                     /*   Toast toast = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
                        View toastView = toast.getView();
                        toastView.setBackgroundResource(R.drawable.toast);
                        toast.show();*/
                    }
                }
}