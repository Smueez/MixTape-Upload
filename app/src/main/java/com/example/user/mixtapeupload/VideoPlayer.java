package com.example.user.mixtapeupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import java.util.ArrayList;
import java.util.List;

public class VideoPlayer extends AppCompatActivity {
    VideoView videoPlayer;
    MediaController controller;
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
    //Handler mHandler = new Handler();
    String name_str;
    int like_count=0;
    TextView comment_txt;
    ListView clistv;
    List<Comment> cList;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoPlayer = findViewById(R.id.videoView);
        clistv = findViewById(R.id.commentList);
        cList = new ArrayList<>();
        comment_txt = findViewById(R.id.editText);
        // seekBar = findViewById(R.id.seekBar);
        song_name = findViewById(R.id.song_name_m);
        art_nam = findViewById(R.id.artist_name_m);
        //playbttn = findViewById(R.id.imageView7);
        likes_count = findViewById(R.id.textView3);
        views_count = findViewById(R.id.textView4);
        song_name_str = getIntent().getExtras().getString("song_name");
        musicURL = getIntent().getExtras().getString("song_url");
        Log.d(TAG, "onCreate: "+song_name_str);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("video").child(song_name_str);
        commentref = FirebaseDatabase.getInstance().getReference().child("video").child(song_name_str);
        storageReference = FirebaseStorage.getInstance().getReference().child("video");
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
        love_icon = findViewById(R.id.imageView10);
        controller = new MediaController(this);
        videoPlayer.setVideoURI(Uri.parse(musicURL));
        controller.setAnchorView(videoPlayer);
        videoPlayer.setMediaController(controller);
        videoPlayer.start();

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
                    if (dataSnapshot.hasChild("video")) {
                        if (dataSnapshot.child("video").hasChild(song_name.getText().toString().trim())) {
                            lover = dataSnapshot.child("video").child(song_name.getText().toString().trim()).getValue(String.class);
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
                    CommentAdapter adapter = new CommentAdapter(VideoPlayer.this, cList);
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
                userdata.child(user_email_str).child("video").child(song_name.getText().toString().trim()).removeValue();
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
                userdata.child(user_email_str).child("video").child(song_name.getText().toString().trim()).setValue("t");
                databaseReference.child("likes").setValue(String.valueOf(like_count + 1));
                lover = "t";
                likes_count.setText(String.valueOf(like_count + 1));
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Login first!",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        if (videoPlayer.isPlaying()){
            videoPlayer.pause();
        }
        Intent intent = new Intent(this,VideoList.class);
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
}
