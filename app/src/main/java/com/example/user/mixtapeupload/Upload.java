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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Upload extends AppCompatActivity implements View.OnClickListener  {
    private static final int SELECT_AUDIO = 2;
    private static final int PERMISSION_REQUEST_CODE = 1,result_loead_image=1,PICK_VIDEO_REQUEST=3;

    private Button buttonChoose;
    private Button buttonUpload,backbttn;
    int secs, mins;
    String songurl,artist,name;
    CheckBox whoshot, newmusic,allmusic;

    //a Uri object to store file path
    private Uri filePath,imagepath;
    DatabaseReference databaseReference,databaseReference1,databaseReference_artist,databaseReference_artist_song;
    private StorageReference storageReference ,storageReference_img;
    TextView song_name,art_name,time_dur;
    Date date;
    String TAG = "upload detail", type_str;
    String strDate;
    long st1,st2,st3;
    ImageView music_icon;
    String ext_img,imageurl;
    int song_count;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        date = new Date();
        //radioGroup = findViewById(R.id.typegrp);
        //String d = date.
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        backbttn = findViewById(R.id.button4);
        music_icon = findViewById(R.id.imageView8);
        //addListenerOnButton();
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference_artist = FirebaseDatabase.getInstance().getReference().child("artist");
        databaseReference_artist_song = FirebaseDatabase.getInstance().getReference().child("artist_songs");
        //attaching listener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        backbttn.setOnClickListener(this);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        strDate = dtf.format(localDate);
        Log.d(TAG, "onCreate: date "+strDate);
        String st1_str = strDate.substring(8,10);
        String st2_str = strDate.substring(5,7);
        String st3_str = strDate.substring(0,4);
        Log.d(TAG, "onCreate: date1 "+st1_str+" "+st2_str+" "+st3_str);
        st1 = Integer.parseInt(st1_str);
        st2 = Integer.parseInt(st2_str);
        st3 = Integer.parseInt(st3_str);
        Log.d(TAG, "onCreate: "+st1+" "+st2+ " "+ st3);
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference_img = FirebaseStorage.getInstance().getReference();
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
        whoshot = findViewById(R.id.checkBox);
        newmusic = findViewById(R.id.checkBox2);
        allmusic = findViewById(R.id.checkBox3);

    }
    public void addListenerOnButton1(View view) {


        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.audiobttn:
                if (checked)
                    type_str = "audio";
                databaseReference = databaseReference1.child(type_str);
                Log.d(TAG, "addListenerOnButton1: "+type_str);
                    break;
            case R.id.videobttn:
                if (checked)
                    type_str = "video";
                databaseReference = databaseReference1.child(type_str);
                Log.d(TAG, "addListenerOnButton1: "+type_str);

                break;
        }

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
    private void showFileChooser_audio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
       // intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_AUDIO);
    }
    private void showFileChooser_video(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_VIDEO_REQUEST);
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
        if (filePath != null && st1 != 0 && st2 != 0 && st3 != 0) {
            //displaying a progress dialog while upload is going on
            String ext = getFileExtention(filePath);
            if (imagepath != null) {
                ext_img = getFileExtention(imagepath);
            }
            Log.d("ext", "uploadFile: ext"+ext);
            Log.d(String.valueOf(1), "uploadFile: " + filePath.toString());
            if(ext == "mp3" || ext == "aac" || ext == "mp4" || ext == "wma" || ext == "3gp" )
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();
                //String path = filePath.getPath().toString();
                MediaMetadataRetriever md = new MediaMetadataRetriever();
                md.setDataSource(this, filePath);
                artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                artist = art_name.getText().toString().trim();

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

                StorageReference riversRef = storageReference.child(type_str).child(name);
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

                if (imagepath != null) {
                    Log.d(TAG, "uploadFile: "+imagepath.toString().trim());
                    StorageReference Sref = storageReference_img.child("image").child(name);
                    Sref.putFile(imagepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: image upload done!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "image upload failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    imageurl = "image" + "/"+ name + "." + ext_img;
                }
                else {
                    imageurl = "empty";
                }
                songurl = name+"."+ext;
                uploaddata(secs,mins,songurl,imageurl,artist,name,st1,st2,st3);
                song_name.setText(name);
                art_name.setText(artist);
                time_dur.setText("Duration: "+mins+"min(s) : "+secs+"sec(s)");

            }
            else
            {
                Toast.makeText(getApplicationContext(),"No file selected",Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(),"No file selected",Toast.LENGTH_SHORT).show();
        }

    }
    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.d(TAG, "onActivityResult: Music loading!");
            Log.d(TAG, "onActivityResult: "+filePath.toString());
        }
        else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.d(TAG, "onActivityResult: Video loading!");
        }
        if (requestCode == result_loead_image && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagepath = data.getData();
            music_icon.setImageURI(imagepath);
            Log.d(TAG, "onActivityResult: Pic is loading!");

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (artist != null) {
           /* databaseReference_artist.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(artist)){
                        song_count = dataSnapshot.child(artist).child("song_count").getValue(Integer.class);
                        song_count = song_count +1;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
            databaseReference_artist_song.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(artist)){
                        song_count = (int) dataSnapshot.child(artist).getChildrenCount();
                        song_count = song_count +1;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void uploaddata(Integer s, Integer m, String surl, String iurl, String ar, String nm, long dd, long mm , long yy){
        String sec,min;
        sec = s.toString();
        min = m.toString();
        if (whoshot.isChecked()){
            Artist_class artist_class = new Artist_class(ar,iurl,song_count);
            databaseReference_artist.child(ar).setValue(artist_class);

            Song song = new Song(sec,min,surl,iurl,ar,"0","0",nm,dd,mm,yy,"Who's Hot");
            databaseReference.child("Who's Hot").child(nm).setValue(song);
            databaseReference_artist_song.child(ar).child(nm).setValue(song);
        }
        if (newmusic.isChecked()){

            Artist_class artist_class = new Artist_class(ar,iurl,song_count);
            databaseReference_artist.child(ar).setValue(artist_class);

            Song song = new Song(sec,min,surl,iurl,ar,"0","0",nm,dd,mm,yy,"New Music");
            databaseReference.child("New Music").child(nm).setValue(song);
            databaseReference_artist_song.child(ar).child(nm).setValue(song);

        }
        if (allmusic.isChecked()){

            Artist_class artist_class = new Artist_class(ar,iurl,song_count);
            databaseReference_artist.child(ar).setValue(artist_class);

            Song song = new Song(sec,min,surl,iurl,ar,"0","0",nm,dd,mm,yy,"All Music");
            databaseReference.child("All Music").child(nm).setValue(song);
            databaseReference_artist_song.child(ar).child(nm).setValue(song);

        }
        if (!whoshot.isChecked() && !newmusic.isChecked() && !allmusic.isChecked()){
            Toast.makeText(getApplicationContext(),"Select where to put your music!",Toast.LENGTH_SHORT).show();
        }

       /* Song song = new Song(sec,min,surl,iurl,ar,"0","0",nm,dd,mm,yy);
        databaseReference.child(nm).setValue(song);*/
       databaseReference_artist.child(ar).child("img_url").setValue(iurl);
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this,home.class);
        if (view == buttonChoose) {
            if (type_str != null) {
                if (type_str == "audio") {
                    showFileChooser_audio();
                }
                else {
                    showFileChooser_video();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please choose file type",Toast.LENGTH_SHORT).show();
            }
        }
        //if the clicked button is upload
        else if (view == buttonUpload) {
            if (type_str != null) {
                Log.d("click", "onClick: ok");
                uploadFile();
            } else {
                Toast.makeText(getApplicationContext(),"Please choose file type",Toast.LENGTH_SHORT).show();
            }
        }
        else if (view == backbttn){
            Log.d("TAG", "onClick: blabla");
            startActivity(intent);
        }
    }
    public void imagechoose(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,result_loead_image);
    }

}
