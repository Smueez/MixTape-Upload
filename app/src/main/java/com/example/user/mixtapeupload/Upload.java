package com.example.user.mixtapeupload;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;

public class Upload extends AppCompatActivity implements View.OnClickListener  {
    private static final int SELECT_AUDIO = 2;
    private static final int PERMISSION_REQUEST_CODE = 1;
    //Buttons
    private Button buttonChoose;
    private Button buttonUpload,backbttn;
    int secs, mins;
    String songurl,artist,name;

    //a Uri object to store file path
    private Uri filePath;
    DatabaseReference databaseReference;
    private StorageReference storageReference ;
    TextView song_name,art_name,time_dur;
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        date = new Date();
        //String d = date.
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        backbttn = findViewById(R.id.button4);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        //attaching listener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        backbttn.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
                Toast.makeText(getApplicationContext(),"check permission ok",Toast.LENGTH_SHORT).show();
            } else {
                requestPermission(); // Code for permission

            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }
        song_name = findViewById(R.id.textView5);
        art_name = findViewById(R.id.textView8);
        time_dur = findViewById(R.id.textView9);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Upload.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.d("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Upload.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Upload.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Upload.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("mp3/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a song"), SELECT_AUDIO);
    }

    private String getFileExtention(Uri imguri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imguri));
    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        super.grantUriPermission(toPackage, uri, modeFlags);
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    //for file uploading
    private void uploadFile() {
        //if there is a file to upload
        Log.d(String.valueOf(1), "uploadFile: "+filePath.toString());
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            String ext = getFileExtention(filePath);
            Log.d("ext", "uploadFile: ext"+ext);
            Log.d(String.valueOf(1), "uploadFile: " + filePath.toString());
            if(ext == "mp3" || ext == "aac" )
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();
                //String path = filePath.getPath().toString();
                MediaMetadataRetriever md = new MediaMetadataRetriever();
                md.setDataSource(this, filePath);
                artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if(artist == null || artist.equals(""))
                {
                    artist = "Unknown";
                }
                name  = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                if (name == null || name.equals("")){
                    name = getFileName(filePath);
                    //String path = getRealPathFromURI(uri);

                    while (name.indexOf(".") > 0) {
                        name = name.substring(0, name.lastIndexOf("."));
                    }
                    if(name.indexOf(".")>0 || name.indexOf("[")> 0|| name.indexOf("]") >0 || name.indexOf("#") >0 || name.indexOf("$") >0 )
                    {
                        Toast.makeText(getApplicationContext(),"Song name can not contain '.', '[' , ']', '#' , '$'",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        return;
                    }
                }
                if(name.indexOf(".")>0 || name.indexOf("[")> 0|| name.indexOf("]") >0 || name.indexOf("#") >0 || name.indexOf("$") >0 )
                {
                    Toast.makeText(getApplicationContext(),"Song name can not contain '.', '[' , ']', '#' , '$'",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    return;
                }
                secs = Integer.parseInt(md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                mins= secs / 60;
                secs =  secs % 60;
                StorageReference riversRef = storageReference.child("songs").child(name);
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying a success toast
                                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
                songurl = "songs/"+name+"."+ext;
                uploaddata(secs,mins,songurl,artist,name);
                song_name.setText(name);
                art_name.setText(artist);
                time_dur.setText("Duration: "+mins+"min(s) : "+secs+"sec(s)");

            }
            else
            {
                Toast.makeText(getApplicationContext(),"No audio file selected",Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(),"No audio file selected",Toast.LENGTH_SHORT).show();
        }

    }
    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

        }
    }
    public void uploaddata(Integer s, Integer m, String surl, String ar,String nm){
        String sec,min,like,view;
        sec = s.toString();
        min = m.toString();

        Song song = new Song(sec,min,surl,ar,"0","0",nm);
        databaseReference.child(nm).setValue(song);
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this,home.class);
        if (view == buttonChoose) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == buttonUpload) {
            Log.d("click", "onClick: ok");
            uploadFile();
        }
        else if (view == backbttn){
            Log.d("TAG", "onClick: blabla");
            startActivity(intent);
        }
    }
}
