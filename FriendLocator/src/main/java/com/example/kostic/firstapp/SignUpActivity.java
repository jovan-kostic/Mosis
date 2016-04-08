package com.example.kostic.firstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.RadioButton;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText et_fname,et_lname,et_phone,et_username,et_password;
    String fname,lname,phone,username,password,team="Green Team";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this);
        pd.setMessage("Registering...");
        pd.setCancelable(false);
        et_fname = (EditText)findViewById(R.id.firstname);
        et_lname = (EditText)findViewById(R.id.lastname);
        et_phone = (EditText)findViewById(R.id.phone);
        et_username = (EditText)findViewById(R.id.username);
        et_password = (EditText)findViewById(R.id.password);

    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.green:
                if (checked)
                    team = "Green Team";
                    break;
            case R.id.red:
                if (checked)
                    team = "Red Team";
                    break;
        }
    }

    public void signUp (View view) {
        if (!validate())
        {
            Toast toast = Toast.makeText(this, "All fields must be valid...", Toast.LENGTH_LONG);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.toast);
            toast.show();
            return;
        }
        pd.show();
        fname = et_fname.getText().toString();
        lname = et_lname.getText().toString();
        phone = et_phone.getText().toString();
        username = et_username.getText().toString();
        password = et_password.getText().toString();
        String method = "signup";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, fname, lname, phone, username, password, team);
    }

    public boolean validate()
    {
        boolean valid = true;
        fname = et_fname.getText().toString();
        lname = et_lname.getText().toString();
        phone = et_phone.getText().toString();
        username = et_username.getText().toString();
        password = et_password.getText().toString();

        if (fname.isEmpty() || fname.length() < 2 || fname.length() > 15) {
            et_fname.setError("between 2 and 15 alphanumeric characters");
            valid = false;
        } else {
            et_fname.setError(null);
        }
        if (lname.isEmpty() || lname.length() < 2 || lname.length() > 15) {
            et_lname.setError("between 2 and 15 alphanumeric characters");
            valid = false;
        } else {
            et_lname.setError(null);
        }
        if (phone.isEmpty() || phone.length() < 6 || phone.length() > 20) {
            et_phone.setError("between 6 and 20 numeric characters");
            valid = false;
        } else {
            et_phone.setError(null);
        }
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

    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
    }
}