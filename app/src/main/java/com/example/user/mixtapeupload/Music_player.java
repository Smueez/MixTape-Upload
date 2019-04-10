package com.example.user.mixtapeupload;


import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Music_player extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Intent serviceintent;
    boolean playmusicbool = true;
    ImageView playbttn;
    String musicURL,TAG = "music player activity";
    TextView song_name, art_nam, likes_count, views_count;
    String song_name_str,sec_str,min_str;
    DatabaseReference databaseReference,userdata,commentref;
    StorageReference storageReference;
    FirebaseUser fuser;
    FirebaseAuth auth;
    String user_email_str;
    String lover = null;
    ImageView love_icon;
    int sec_int,min_int;
   // SeekBar seekBar;
    Handler mHandler = new Handler();
    String name_str;
    int like_count=0;
    TextView comment_txt;
    ListView clistv;
    List<Comment> cList;
    LinearLayout linearLayout;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        try {
            serviceintent = new Intent(this,MyBackgroundService.class);

        }catch (Exception e){
            e.printStackTrace();
        }
        clistv = findViewById(R.id.commentList);
        cList = new ArrayList<>();
        comment_txt = findViewById(R.id.editText);
       // seekBar = findViewById(R.id.seekBar);
        song_name = findViewById(R.id.song_name_m);
        art_nam = findViewById(R.id.artist_name_m);
        playbttn = findViewById(R.id.imageView7);
        likes_count = findViewById(R.id.textView3);
        views_count = findViewById(R.id.textView4);
        song_name_str = getIntent().getExtras().getString("song_name");
        musicURL = getIntent().getExtras().getString("song_url");
        Log.d(TAG, "onCreate: "+song_name_str);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs").child(song_name_str);
        commentref = FirebaseDatabase.getInstance().getReference().child("songs").child(song_name_str);
        storageReference = FirebaseStorage.getInstance().getReference().child("songs");
        auth = FirebaseAuth.getInstance();
        userdata = FirebaseDatabase.getInstance().getReference().child("profile");
        linearLayout = findViewById(R.id.comment_layout);
        if (auth.getCurrentUser() != null) {
            fuser = auth.getCurrentUser();
            user_email_str = fuser.getEmail();
            user_email_str = user_email_str.replace(".com", "");
        }
        else {
            linearLayout.setVisibility(View.GONE);
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        love_icon = findViewById(R.id.imageView10);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicURL);


        } catch (IOException e) {
            e.printStackTrace();
        }
        playbttn.setImageResource(R.drawable.pause);
        playmusicbool = false;

        // serviceintent.putExtra("musicLink",musicLink);
        try {
            mediaPlayer.prepare();

        } catch (Exception e){
            e.printStackTrace();
        }

        mediaPlayer.start();
        //seekBar.setMax(mediaPlayer.getDuration());
        Log.d(TAG, "onCreate: "+mediaPlayer.getDuration());

//Make sure you update Seekbar on UI thread
       /* Music_player.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    Log.d(TAG, "run: "+String.valueOf(mCurrentPosition));
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    Log.d(TAG, "onProgressChanged: "+String.valueOf(progress*1000));
                    mediaPlayer.seekTo(progress *1000);
                    seekBar.setProgress(progress);
                }
            }
        });*/
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

    }



    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Music_player.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

        if (ActivityCompat.shouldShowRequestPermissionRationale(Music_player.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Music_player.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Music_player.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                song_name.setText(dataSnapshot.child("sname").getValue(String.class));
                art_nam.setText(dataSnapshot.child("artist").getValue(String.class));
                likes_count.setText(dataSnapshot.child("likes").getValue(String.class));
                like_count =  Integer.valueOf(likes_count.getText().toString().trim());
                views_count.setText(dataSnapshot.child("views").getValue(String.class));
                sec_str = dataSnapshot.child("sec").getValue(String.class);
                min_str = dataSnapshot.child("min").getValue(String.class);
                sec_int = Integer.valueOf(sec_str);
                min_int = Integer.valueOf(min_str);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (auth.getCurrentUser() != null) {
            userdata.child(user_email_str).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name_str = dataSnapshot.child("name").getValue(String.class);
                    if (dataSnapshot.hasChild("songs")) {
                        if (dataSnapshot.child("songs").hasChild(song_name.getText().toString().trim())) {
                            lover = dataSnapshot.child("songs").child(song_name.getText().toString().trim()).getValue(String.class);
                            love_icon.setImageResource(R.drawable.loved);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
            commentref.child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clistv.setAdapter(null);
                    cList.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: loop e dhuktese!");
                            Comment comment = ds.getValue(Comment.class);
                            cList.add(comment);
                        }
                        CommentAdapter adapter = new CommentAdapter(Music_player.this, cList);
                        clistv.setAdapter(adapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    public void liked(View view){
        if (auth.getCurrentUser() != null) {
            if (lover != null) {
                love_icon.setImageResource(R.drawable.uclickedlove);
                userdata.child(user_email_str).child("songs").child(song_name.getText().toString().trim()).removeValue();
                if (like_count != 0) {
                    databaseReference.child("likes").setValue(String.valueOf(like_count - 1));
                    likes_count.setText(String.valueOf(like_count - 1));

                } else {
                    databaseReference.child("likes").setValue(String.valueOf(like_count));
                    likes_count.setText(String.valueOf(like_count));
                }
                lover = null;

            } else {
                love_icon.setImageResource(R.drawable.loved);
                userdata.child(user_email_str).child("songs").child(song_name.getText().toString().trim()).setValue("t");
                databaseReference.child("likes").setValue(String.valueOf(like_count + 1));
                lover = "t";
                likes_count.setText(String.valueOf(like_count + 1));
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Login first!",Toast.LENGTH_LONG).show();
        }
    }
    public void playorstop(View view){
        if (playmusicbool) {
            Log.d(TAG, "playorstop: player playing!");
            playbttn.setImageResource(R.drawable.pause);
            playmusicbool = false;
           //mediaPlayer.reset();
           // serviceintent.putExtra("musicLink",musicLink);
            if (!mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.prepare();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mediaPlayer.start();

        }
        else {
            Log.d(TAG, "playorstop: not playing!");
            playbttn.setImageResource(R.drawable.playmusic);
            mediaPlayer.pause();
            playmusicbool = true;

        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press

        Intent intent = new Intent(this,home.class);
        startActivity(intent);
    }
    public void comment(View view){
        if (comment_txt.getText().toString().trim() != null){
            Comment comment = new Comment(name_str,comment_txt.getText().toString().trim());
            databaseReference.child("comments").child(user_email_str).setValue(comment);
        }
    }
    public void share(View view){
        String message = musicURL;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, "Share Music!"));
    }
    public void download(View view){
        Log.d("ok", "download: 1");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        //notificationDownload();
        File rootPath = new File(Environment.getExternalStorageDirectory(), "MixTape");
        //File rootPath = new File(this.getFilesDir()," mix");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
       /* File localFile = null;
        try {
            localFile = File.createTempFile("Audio", "mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        final File localFile = new File(rootPath,song_name_str+".mp3");

        storageReference.child(song_name_str).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Download completed!\nCheck MixTape folder",Toast.LENGTH_LONG).show();
                Log.d("downloaded file", "onSuccess: downloaded!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
    }

    public void notificationDownload(){
        Log.d(TAG, "notificationDownload: fuction called!");
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(Music_player.this,"MixTape");
        mBuilder.setContentTitle("MixTape Upload")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.welcomesrecean);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        Log.d(TAG, "run: thread works");
                        for (incr = 0; incr <= 100; incr+=5) {
                            // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(1, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            try {
                                // Sleep for 1 second
                                Log.d(TAG, "run: thred working!");
                                Thread.sleep(1*1000);
                            } catch (InterruptedException e) {
                                Log.d("TAG", "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download completed")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(1, mBuilder.build());
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();

    }
}
