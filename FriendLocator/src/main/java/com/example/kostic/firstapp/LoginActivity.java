package com.example.kostic.firstapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText et_username,et_password;
    String username, password;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(this);
        pd.setMessage("Authenticating...");
        pd.setCancelable(false);
        et_username = (EditText)findViewById(R.id.username);
        et_password = (EditText)findViewById(R.id.password);

       Button button_sign_up= (Button) findViewById(R.id.sign_up_button);
        button_sign_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent open_signup = new Intent();
                open_signup.setAction("com.example.kostic.firstapp.Signup");
                open_signup.addCategory("android.intent.category.DEFAULT");
                startActivity(open_signup);
            }
        });

    }

    public void SignIn(View view)
    {
        if (!validate())
        {
            Toast toast = Toast.makeText(this, "All fields must be valid...", Toast.LENGTH_LONG);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.toast);
            toast.show();
            return;
        }
        pd.show();
        username = et_username.getText().toString();
        password = et_password.getText().toString();
        String method = "signin";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, username, password);
    }

    public boolean validate()
    {
        boolean valid = true;
        username = et_username.getText().toString();
        password = et_password.getText().toString();
        if (username.isEmpty() || username.length() < 4 || username.length() > 15) {
            et_username.setError("between 4 and 15 alphanumeric characters");
            valid = false;
        } else {
            et_username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 15) {
            et_password.setError("between 4 and 15 alphanumeric characters");
            valid = false;
        } else {
            et_password.setError(null);
        }
        return valid;
    }

    public void startMain()
    {
        Intent open_main = new Intent();
        open_main.setAction("com.example.kostic.firstapp.Main");
        open_main.addCategory("android.intent.category.DEFAULT");
        open_main.putExtra("username", username);
        startActivity(open_main);
        finish();
    }
}

