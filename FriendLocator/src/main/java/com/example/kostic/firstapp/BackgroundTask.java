package com.example.kostic.firstapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class BackgroundTask extends AsyncTask<String, Void, String> {

    LoginActivity ctx1;
    SignUpActivity ctx2;

    BackgroundTask(LoginActivity ctx) {
        this.ctx1 = ctx;
    }
    BackgroundTask(SignUpActivity ctx) {
        this.ctx2 = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String sign_up_url = "http://192.168.1.4/fl_server/sign_up.php";
        String sign_in_url = "http://192.168.1.4/fl_server/sign_in.php";
        String method = params[0];

        if (method.equals("signup"))
        {
            String fname = params[1];
            String lname = params[2];
            String phone = params[3];
            String username = params[4];
            String password = params[5];

            try {
                URL url = new URL(sign_up_url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(2000);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("fname", "UTF-8") +"="+URLEncoder.encode(fname, "UTF-8")+"&"+
                        URLEncoder.encode("lname", "UTF-8") +"="+URLEncoder.encode(lname, "UTF-8")+"&"+
                        URLEncoder.encode("phone", "UTF-8") +"="+URLEncoder.encode(phone, "UTF-8")+"&"+
                        URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8") +"="+URLEncoder.encode(password, "UTF-8");
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
            }
        }
        else if(method.equals("signin"))
        {
            String username = params[1];
            String password = params[2];
            try {
            URL url = new URL(sign_in_url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(2000);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter((outputStream))));
                String data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
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
            }

        }
            return "Connection error...";
      }

            @Override
            protected void onProgressUpdate(Void...values){
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result){
                if (ctx2!=null) // sign UP
                {
                    if (result.startsWith("Thank you for registering!"))
                    {
                        NavUtils.navigateUpFromSameTask(ctx2);
                    }
                    ctx2.pd.dismiss();
                    Toast toast = Toast.makeText(ctx2, result, Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast);
                    toast.show();
                }
                else // sign IN
                {
                    if (result.startsWith("Welcome"))
                    {
                      ctx1.startMain();
                    }
                    ctx1.pd.dismiss();
                    Toast toast = Toast.makeText(ctx1, result, Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast);
                    toast.show();
                }
            }
}