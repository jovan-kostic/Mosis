package com.example.kostic.firstapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.sip.SipSession;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class ProfileActivity extends AppCompatActivity {

    String user,username_clicked;
    TextView fname,lname,phone,username,password;
    ImageView image;
    Button upload;
    ProgressDialog pd;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username_clicked = getIntent().getStringExtra("username");
        user = getIntent().getStringExtra("user");

        fname = (TextView)findViewById(R.id.firstname);
        lname = (TextView)findViewById(R.id.lastname);
        phone = (TextView)findViewById(R.id.phone);
        username = (TextView)findViewById(R.id.username);
        image = (ImageView)findViewById(R.id.image);

        upload = (Button)findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        if (user.equals(username_clicked))
        {
            upload.setVisibility(View.VISIBLE);
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Retrieving information...");
        pd.setCancelable(false);
        pd.show();
        ProfileTask profileTask = new ProfileTask(this);
        profileTask.execute(username_clicked);
        Toast toast = Toast.makeText(this, "Retrieving profile photo...", Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.toast);
        toast.show();
        PhotoDownloaderTask photoDownloaderTask = new PhotoDownloaderTask(this);
        photoDownloaderTask.execute(username_clicked);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Toast toast = Toast.makeText(this, "Uploading: " + picturePath, Toast.LENGTH_LONG);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.toast);
            toast.show();

            //resize bitmap
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o);

                int width_tmp = o.outWidth
                        , height_tmp = o.outHeight;
                int scale = 1;

                while(true) {
                    if(width_tmp / 2 < 400 || height_tmp / 2 < 400)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }

                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o2);

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String imgString = Base64.encodeToString(b, Base64.NO_WRAP);
                image.setImageBitmap(bitmap);
                PhotoUploaderTask photoUploaderTask = new PhotoUploaderTask(this);
                photoUploaderTask.execute(user, imgString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}

