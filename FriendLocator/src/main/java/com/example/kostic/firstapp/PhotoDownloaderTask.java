package com.example.kostic.firstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class PhotoDownloaderTask extends AsyncTask<String, Void, String> {

    ProfileActivity ctx;
    String user;
    String path;
    Bitmap bitmap;


    PhotoDownloaderTask(ProfileActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {

        path = "http://192.168.1.4/fl_server/photos/";
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            user = params[0];
            String complete_path = path.concat(user)+".bmp";
            URL url = new URL(complete_path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(2000);

            httpURLConnection.connect();
            InputStream input = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            return "OK";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Retrieving profile photo failed...";
}
            @Override
            protected void onProgressUpdate(Void...values){
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result){
                if (result.startsWith("Retrieving profile photo failed..."))
                {
                    Toast toast = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast);
                    toast.show();
                }
                else
                {
                    ctx.image.setImageBitmap(bitmap);
                }
                ctx.pd.dismiss();
            }

}