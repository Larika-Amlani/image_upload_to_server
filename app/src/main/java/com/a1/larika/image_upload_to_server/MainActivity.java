package com.a1.larika.image_upload_to_server;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ImageView camera, gallery, upload, result;
    CameraPhoto cp;
Uri uri;
    Bitmap bitmap;
TextView tv1;
    String login_url = "http://192.168.1.103/ImageUpload/image_insert.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestStoragePermission();
        cp = new CameraPhoto(this);
        tv1 = (TextView) findViewById(R.id.textView);
        camera = (ImageView) findViewById(R.id.imageView);
        gallery = (ImageView) findViewById(R.id.imageView2);
        upload = (ImageView) findViewById(R.id.imageView3);
        result = (ImageView) findViewById(R.id.imageView4);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (camera.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(camera, 1234);
                else
                    Toast.makeText(getApplicationContext(), "something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(in1, 1233);

            }
        });
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
String path=getPath(uri);
                String Uploadid= UUID.randomUUID().toString();
                try {
                    new MultipartUploadRequest(getApplicationContext(),Uploadid,login_url).addFileToUpload(path,"image").setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2).startUpload();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1223);
    }

    private String getPath(Uri uri)
    {
        Cursor cursor=getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String doc_id=cursor.getString(0);
        Log.i("first",doc_id);
        doc_id=doc_id.substring(doc_id.lastIndexOf(":")+1);
        Log.i("second",doc_id);
        cursor.close();
        cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,MediaStore.Images.Media._ID+"=?",new String[]{doc_id},null );
        cursor.moveToFirst();
        String path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;

    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 1223) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(resultCode == RESULT_OK){
        if (requestCode == 1234 ) {
            Bundle extras = data.getExtras();
            Bitmap bp = (Bitmap) extras.get("data");
            result.setImageBitmap(bp);
        }
           else if(requestCode == 1233) {
                uri = data.getData();
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                result.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        }
    }

    }
