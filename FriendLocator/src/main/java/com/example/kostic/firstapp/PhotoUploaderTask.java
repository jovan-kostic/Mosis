package com.example.kostic.firstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

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

public class PhotoUploaderTask extends AsyncTask<String, Void, String> {

    ProfileActivity ctx;
    String user;
    String imgString;
    String upload_url;
    Bitmap bitmap;


    PhotoUploaderTask(ProfileActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {

        upload_url = "http://192.168.1.4/fl_server/upload.php";
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            user = params[0];
            imgString = params[1];
            URL url = new URL(upload_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(15000);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("user", "UTF-8") +"="+URLEncoder.encode(user, "UTF-8")+"&"+
                    URLEncoder.encode("image", "UTF-8") +"="+URLEncoder.encode(imgString, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String response = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
            {
                response = line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return response;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Uploading profile photo failed...";
}
            @Override
            protected void onProgressUpdate(Void...values){
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result){
                if (result.startsWith("Uploading profile photo failed..."))
                {
                    Toast toast = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast);
                    toast.show();
                }
                else
                {

                }
                ctx.pd.dismiss();
            }

}