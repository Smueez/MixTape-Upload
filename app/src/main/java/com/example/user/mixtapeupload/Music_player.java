package com.example.user.mixtapeupload;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.user.mixtapeupload.App.CHANNEL_1_ID;

public class Music_player extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Intent serviceintent;
    boolean playmusicbool = true;
    ImageView playbttn;
    CircleImageView songImage;
    String musicURL,TAG = "music player activity";
    TextView song_name, art_nam, likes_count, views_count;
    String song_name_str,sec_str,min_str;
    DatabaseReference databaseReference,userdata,commentref,databaseadmin,databaseReference_edit,databaseReference_artist;
    StorageReference storageReference,storageReference1;
    FirebaseUser fuser;
    FirebaseAuth auth;
    String user_email_str;
    String lover = null,artist_total_like_str;
    ImageView love_icon;
    int sec_int,min_int;
    CheckBox checkBox1,checkBox2;
    // SeekBar
    SeekBar seekBar;
    TextView seekText;
    int mediaduration;
    int realTimeDuration;
    int secondPercent = 1;
    int inc=0,artist_like_count=0,song_like_count;
    private static final int MY_NOTIFICATION_SERVICE = 2;
    LinearLayout linearLayout;
    FloatingActionButton button_exit,button_edit;
    NotificationCompat.Builder builder;


    public long day_storage,mounth_storage,year_storage;

    Handler mHandler = new Handler();
    String name_str,urist1,file_place,muser_str,song_total_like_str;
    int like_count=0;
    TextView comment_txt;
    ListView clistv;
    List<Comment> cList;
    LinearLayout linearLayout_edit;
    Animation rotate;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder mBuilder;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        songImage = findViewById(R.id.imageView5);
        auth = FirebaseAuth.getInstance();
        fuser = auth.getCurrentUser();
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
        file_place = getIntent().getExtras().getString("file_place");
        Log.d(TAG, "onCreate: song name "+song_name_str);
        Log.d(TAG, "onCreate: music link "+musicURL);
        Log.d(TAG, "onCreate: file place "+file_place);
        checkBox1 = findViewById(R.id.checkBox6);
        checkBox2 = findViewById(R.id.checkBox5);
        button_edit = findViewById(R.id.floatingActionButton7);
        button_exit = findViewById(R.id.exit_edit);
        linearLayout_edit = findViewById(R.id.edit_layout);
        notificationManager = NotificationManagerCompat.from(this);
        if (auth.getCurrentUser() != null) {

            String muser_str1 = auth.getCurrentUser().getEmail();
            muser_str = muser_str1.replace(".com","");
            Log.d(TAG, "onCreate: " + muser_str);
            databaseadmin = FirebaseDatabase.getInstance().getReference().child("profile").child(muser_str);

        }
        if (file_place == "Who's Hot"){
            checkBox1.setText("All Music");
            checkBox2.setText("New Music");
        }
        else if (file_place == "All Music"){
            checkBox1.setText("Who's Hot");
            checkBox2.setText("New Music");
        }
        else {
            checkBox1.setText("Who's Hot");
            checkBox2.setText("All Music");
        }
        Log.d(TAG, "onCreate: "+song_name_str);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("audio").child(file_place).child(song_name_str);
        databaseReference_artist = FirebaseDatabase.getInstance().getReference();
        commentref = FirebaseDatabase.getInstance().getReference().child("audio").child(file_place).child(song_name_str);
        databaseReference_edit = FirebaseDatabase.getInstance().getReference().child("audio");
        storageReference = FirebaseStorage.getInstance().getReference().child("audio");
        storageReference1 = FirebaseStorage.getInstance().getReference().child("image");
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

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(99);
        seekText = findViewById(R.id.seekText);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mediaPlayer.isPlaying()){

                    SeekBar seekBar = (SeekBar)view;
                    int playposition = (mediaduration/100) * seekBar.getProgress();
                    realTimeDuration += mediaPlayer.getCurrentPosition() - playposition;
                    mediaPlayer.seekTo(playposition);

                }

                return false;
            }
        });

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

        mediaduration = mediaPlayer.getDuration();
        realTimeDuration = mediaduration;


        mediaPlayer.start();

        updateSeekbar();

        //seekBar.setMax(mediaPlayer.getDuration());
        Log.d(TAG, "onCreate: "+mediaPlayer.getDuration());


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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            Log.d("notification","not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d("notification","request permision");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                        MY_NOTIFICATION_SERVICE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                Log.d("notification","request permision");
            }
        } else {
            sendOnChannel1();
        }
        hide_Keayboard();
    }
    public void hide_Keayboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Log.d(TAG, "performSearch: keyboard hiden!");
        }

    }
    public void sendOnChannel1() {
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, Music_player.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.welcomesrecean)
                .setContentTitle("MixTape Upload")
                .setContentText("Now " + song_name_str + " is playing ...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOngoing(true)
                .build();

        notificationManager.notify(1, notification);
    }

    public static class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView songImage;


        public DownLoadImageTask(ImageView songImage){
            this.songImage = songImage;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            songImage.setImageBitmap(result);
        }
    }


    private void updateSeekbar() {
        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaduration) * 100));
        if(mediaPlayer.isPlaying()){
            Runnable updater = new Runnable() {
                @Override
                public void run() {
                    updateSeekbar();
                    realTimeDuration -= 1000;
                    seekText.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(realTimeDuration),
                            TimeUnit.MILLISECONDS.toSeconds(realTimeDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realTimeDuration))));


                    if(secondPercent < 99){
                        secondPercent  = secondPercent + inc;
                        inc += 1;
                        seekBar.setSecondaryProgress(secondPercent);
                    }


                }
            };
            mHandler.postDelayed(updater,1000);
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
            case MY_NOTIFICATION_SERVICE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendOnChannel1();
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
        urist1 = getIntent().getExtras().getString("music_image_url");
        if(urist1 != "empty" && urist1 != null) {
            Log.d(TAG, "onDataChange: urist "+urist1);
            storageReference1.child(urist1).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d(TAG, "onSuccess: got url of image!");
                    new Music_player.DownLoadImageTask(songImage).execute(uri.toString());
                    Log.d(TAG, "onSuccess: "+uri.toString());
                    // progressBar1.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getMessage());
                }
            });
        }
        else {
            songImage.setImageResource(R.drawable.cd);
        }
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
                day_storage = dataSnapshot.child("day").getValue(Long.class);
                mounth_storage = dataSnapshot.child("mounth").getValue(Long.class);
                year_storage = dataSnapshot.child("year").getValue(Long.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference_artist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*song_total_like_str = dataSnapshot.child("artist_song").child(art_nam.getText().toString().trim()).child(song_name_str).child("likes").getValue(String.class);
                song_like_count = Integer.valueOf(song_total_like_str);*/
                artist_total_like_str = dataSnapshot.child("artist").child(art_nam.getText().toString().trim()).child("total_likes").getValue(String.class);
                artist_like_count = Integer.valueOf(artist_total_like_str);
                /*if (artist_total_like_str != null) {
                    song_like_count = Integer.valueOf(song_total_like_str);
                    artist_like_count = Integer.valueOf(artist_total_like_str);
                }*/
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
                    if (dataSnapshot.hasChild("audio")) {
                        if (dataSnapshot.child("audio").hasChild(song_name.getText().toString().trim())) {
                            lover = dataSnapshot.child("audio").child(song_name.getText().toString().trim()).getValue(String.class);
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




        if (auth.getCurrentUser() != null) {
            databaseadmin.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("admin")){
                        button_edit.setVisibility(View.VISIBLE);
                    }
                    else {
                        button_edit.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
           //   Log.d(TAG, "onDataChange: admin2 " +admin);

        }
        else {
            button_edit.setVisibility(View.GONE);
        }
    }

    public void liked(View view){
        if (auth.getCurrentUser() != null) {

            if (lover != null) {
                love_icon.setImageResource(R.drawable.uclickedlove);
                userdata.child(user_email_str).child("audio").child(song_name.getText().toString().trim()).removeValue();
                if (like_count != 0) {
                    databaseReference.child("likes").setValue(String.valueOf(like_count - 1));
                    likes_count.setText(String.valueOf(like_count - 1));
                    databaseReference_artist.child("artist_songs").child(art_nam.getText().toString().trim()).child(song_name_str).child("likes").setValue(String.valueOf(like_count - 1));
                    databaseReference_artist.child("artist").child(art_nam.getText().toString().trim()).child("total_likes").setValue(String.valueOf(artist_like_count - 1));

                } else {
                    databaseReference.child("likes").setValue(String.valueOf(like_count));
                    likes_count.setText(String.valueOf(like_count));
                    databaseReference_artist.child("artist_songs").child(art_nam.getText().toString().trim()).child(song_name_str).child("likes").setValue(String.valueOf(like_count));
                    databaseReference_artist.child("artist").child(art_nam.getText().toString().trim()).child("total_likes").setValue(String.valueOf(artist_like_count));                }
                lover = null;

            } else {
                love_icon.setImageResource(R.drawable.loved);
                userdata.child(user_email_str).child("audio").child(song_name.getText().toString().trim()).setValue("t");
                databaseReference.child("likes").setValue(String.valueOf(like_count + 1));
                databaseReference_artist.child("artist_songs").child(art_nam.getText().toString().trim()).child(song_name_str).child("likes").setValue(String.valueOf(like_count + 1));
                databaseReference_artist.child("artist").child(art_nam.getText().toString().trim()).child("total_likes").setValue(String.valueOf(artist_like_count + 1));
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
            updateSeekbar();
        }
        else {
            Log.d(TAG, "playorstop: not playing!");
            playbttn.setImageResource(R.drawable.playmusic);
            mediaPlayer.pause();
            playmusicbool = true;
            updateSeekbar();
        }
    }
    Artist_list artist_list = new Artist_list();
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        if (!artist_list.cliked) {
            Intent intent = new Intent(this, home.class);

            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this,Artist_list.class);
            startActivity(intent);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


// notificationId is a unique int for each notification that you must define
        notificationManager.cancel(0);
        notificationManager.cancel(1);
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
        progressDialog.setTitle("Downloading...");
        progressDialog.show();
        //notificationDownload();
        File rootPath = new File(Environment.getExternalStorageDirectory(), "MixTape");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

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
    public  void edit_music(View view){
        linearLayout_edit.setVisibility(View.VISIBLE);
        button_exit.setVisibility(View.VISIBLE);
        button_edit.animate().alpha((float) 0.3);
    }

    public void exit_edit(View view){
        linearLayout_edit.setVisibility(View.GONE);
        button_exit.setVisibility(View.GONE);
        button_edit.animate().alpha(1);
    }
    public void addmusic(View view){
        if (checkBox1.isChecked()){
            Song song = new Song(sec_str,min_str,musicURL,urist1,art_nam.getText().toString().trim(),likes_count.getText().toString().trim(),views_count.getText().toString().trim(),song_name_str,day_storage,mounth_storage,year_storage,checkBox1.getText().toString().trim());
            databaseReference_edit.child(checkBox1.getText().toString().trim()).child(song_name_str).setValue(song);
        }
        if (checkBox2.isChecked()){
            Song song = new Song(sec_str,min_str,musicURL,urist1,art_nam.getText().toString().trim(),likes_count.getText().toString().trim(),views_count.getText().toString().trim(),song_name_str,day_storage,mounth_storage,year_storage,checkBox2.getText().toString().trim());
            databaseReference.child(checkBox2.getText().toString().trim()).child(song_name_str).setValue(song);
        }
        if (!checkBox2.isChecked() && !checkBox1.isChecked()){
            Toast.makeText(getApplicationContext(),"please select a file to add music!",Toast.LENGTH_SHORT).show();
        }
    }
    public void delete(View view){
        final Intent intent = new Intent(this,home.class);
        //auth.signOut();
        databaseReference_edit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("audio").child("All Music").hasChild(song_name_str)){
                    databaseReference_edit.child("audio").child("All Music").child(song_name_str).removeValue();

                }
                if (dataSnapshot.child("audio").child("New Music").hasChild(song_name_str)){
                    databaseReference_edit.child("audio").child("New Music").child(song_name_str).removeValue();
                }
                if (dataSnapshot.child("audio").child("Who's Hot").hasChild(song_name_str)){
                    databaseReference_edit.child("audio").child("Who's Hot").child(song_name_str).removeValue();
                }
                databaseReference_artist.child("artist_songs").child(art_nam.getText().toString().trim()).child(song_name_str).removeValue();
                Toast.makeText(getApplicationContext(),"The Audio song has deleted!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storageReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"deactivated!",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }
}
