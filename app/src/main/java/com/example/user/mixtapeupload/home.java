package com.example.user.mixtapeupload;


import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String view_str,imgurl,return_place;
    Uri filePath;
   StorageReference storageReference;
    private static final int SELECT_AUDIO = 2;
    DatabaseReference databaseReference,databaseReferencelist,databaseadmin,databaseReference_artist,databaseReference_artist_song;
    ListView listView;
    List<Song> songList;
    String TAG = "home content";
    ImageView imageView;
    String artist,name,song_name,musicLink;
    Intent intent;
    int view_count ;
    FirebaseAuth auth;
    ProgressBar progressBar;
    FirebaseAuth mauth;
    Intent intent1;
    Button hot_bttn,new_bttn,topViewbttn,all_bttn;
    GridView gridView;
    String file_type = "audio",muser_str,admin,file_place="New Music",artist_view_str;
    ImageView imageView1;
    int artist_view;
    String song_artist_view;
    int song_artist_view_count,position_item = -1;
    final Semaphore semaphore = new Semaphore(0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try{
            return_place = getIntent().getExtras().getString("return_type");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "onCreate: exception "+e.getMessage().toString());
        }

        if (return_place == null){
            return_place = "New Music";
        }
        Log.d(TAG, "onCreate: return string "+ return_place);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            String muser_str1 = auth.getCurrentUser().getEmail();
            muser_str = muser_str1.replace(".com","");
            Log.d(TAG, "onCreate: " + muser_str);
            databaseadmin = FirebaseDatabase.getInstance().getReference().child("profile").child(muser_str);

        }
        storageReference = FirebaseStorage.getInstance().getReference().child(file_type);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(file_type);
        databaseReference_artist = FirebaseDatabase.getInstance().getReference().child("artist");
        databaseReference_artist_song = FirebaseDatabase.getInstance().getReference().child("artist_songs");

        listView = findViewById(R.id.listview);
        gridView = findViewById(R.id.gridview);
        songList = new ArrayList<>();
        databaseReferencelist = FirebaseDatabase.getInstance().getReference().child(file_type);
        if (file_type == "audio") {
            intent = new Intent(this, Music_player.class);
        } else {
            intent = new Intent(this, VideoPlayer.class);

        }
        //file_place = "New Music";
        imageView1 = findViewById(R.id.imageView16);
        progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.VISIBLE);
        mauth = FirebaseAuth.getInstance();
        intent1 = new Intent(this,MainActivity.class);
        new_bttn = findViewById(R.id.newbttn);
        all_bttn = findViewById(R.id.all);
        hot_bttn = findViewById(R.id.hot);
        topViewbttn = findViewById(R.id.viewed);
        hot_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        new_bttn.setBackgroundColor(getResources().getColor(R.color.clickedPrimary));
        topViewbttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        all_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
           /* Intent intent = new Intent(this,Music_player.class);
            startActivity(intent);*/
            Log.d(TAG, "onBackPressed: nothing");
            if(mauth.getCurrentUser() == null){
                startActivity(intent1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (mauth.getCurrentUser() != null) {
                auth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Login first!",Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Intent intent1 = new Intent(this,home.class);
        if (id == R.id.nav_camera) {
            // Handle the camera action
            if (auth.getCurrentUser() != null){
                Intent intent = new Intent(this,profile.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Log in first!",Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_gallery){
            if (mauth.getCurrentUser() != null) {
                auth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Login first!",Toast.LENGTH_LONG).show();
            }
        }

        else if (id == R.id.video){
            Intent intent = new Intent(this,VideoList.class);
            startActivity(intent);
        }
        else if (id == R.id.artist_show){
            Intent intent = new Intent(this, Artist_list.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public int sCompare(String str1, String str2)
    {
        int res = str1.compareTo(str2);
        if(res > 0) return 1;
        else return -1;
    }
    public void whatsHot(View view)
    {
        gridView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        hot_bttn.setBackgroundColor(getResources().getColor(R.color.clickedPrimary));
        new_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        topViewbttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        all_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        file_place = "Who's Hot";
        return_place = "Who's Hot";
        databaseReferencelist.child(file_place).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listView.setAdapter(null);
                songList.clear();
                Log.d(TAG, "onDataChange driver: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Song song = ds.getValue(Song.class);
                    songList.add(song);
                }
                Collections.sort(songList, new Comparator<Song>() {
                    public int compare(Song s1, Song s2) {
                        int res = sCompare(s1.likes,s2.likes);
                        if(res == 1) return -1;
                        else return 1;
                    }
                });
                Listadapter listadapter = new Listadapter(home.this,songList);
                listView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void mostPlayed(View view)
    {
        gridView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        hot_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        new_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        topViewbttn.setBackgroundColor(getResources().getColor(R.color.clickedPrimary));
        all_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        file_place = "All Music";
        return_place = "Most Viewed";
        databaseReferencelist.child(file_place).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listView.setAdapter(null);
                songList.clear();
                Log.d(TAG, "onDataChange driver: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Song song = ds.getValue(Song.class);
                    songList.add(song);
                }
                Collections.sort(songList, new Comparator<Song>() {
                    public int compare(Song s1, Song s2) {
                        int res = sCompare(s1.views,s2.views);
                        if(res == 1) return -1;
                        else return 1;
                    }
                });
                Listadapter listadapter = new Listadapter(home.this,songList);
                listView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void whatsNew(View view)
    {
        gridView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        hot_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        new_bttn.setBackgroundColor(getResources().getColor(R.color.clickedPrimary));
        topViewbttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        all_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        file_place = "New Music";
        return_place = "New Music";
        databaseReferencelist.child(file_place).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gridView.setAdapter(null);
                songList.clear();
                Log.d(TAG, "onDataChange driver: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Song song = ds.getValue(Song.class);
                    songList.add(song);
                }
                Collections.sort(songList, new Comparator<Song>() {
                    public int compare(Song s1, Song s2) {
                        int res = classCompare(s1,s2);
                        if(res == 1) return -1;
                        else return 1;
                    }
                });
                Listadapter listadapter = new Listadapter(home.this,songList);
                gridView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void all_songs(View view){
        hot_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        new_bttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        topViewbttn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        all_bttn.setBackgroundColor(getResources().getColor(R.color.clickedPrimary));
        gridView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        file_place = "All Music";
        return_place = "All Music";
        databaseReferencelist.child(file_place).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listView.setAdapter(null);
                songList.clear();
                Log.d(TAG, "onDataChange driver: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Song song = ds.getValue(Song.class);
                    songList.add(song);
                }
                Collections.sort(songList, new Comparator<Song>() {
                    public int compare(Song s1, Song s2) {
                        int res = sCompare(s1.sname,s2.sname);
                        return res;
                    }
                });
                Listadapter listadapter = new Listadapter(home.this,songList);
                listView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int classCompare(Song a, Song b)
    {
        if(a.year == b.year)
        {
            if(a.mounth == b.year)
            {
                if(a.day > b.day)
                {
                    return 1;
                }
                else return -1;
            }
            else if(a.mounth > b.mounth) return 1;
            else return -1;
        }
        else if(a.year > b.year) return 1;
        else return -1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + file_place);
        databaseReferencelist.child(file_place).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gridView.setAdapter(null);
                songList.clear();
                Log.d(TAG, "onDataChange driver: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Song song = ds.getValue(Song.class);
                    songList.add(song);
                }
                Collections.sort(songList, new Comparator<Song>() {
                    public int compare(Song s1, Song s2) {
                        int res = classCompare(s1,s2);
                        if(res == 1) return -1;
                        else return 1;
                    }
                });
                Listadapter listadapter = new Listadapter(home.this,songList);
                gridView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onStart: artist views1 "+artist_view_str);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // clicked item
                    if (position_item == -1){
                        position_item = position;
                    }
                    else {
                       return;
                    }
                    song_name = songList.get(position).getSname();
                    artist = songList.get(position).getArtist();
                    intent.putExtra("song_name", song_name);

                    Log.d(TAG, "onItemClick: position" + songList.get(position).toString());
                    Log.d(TAG, "check: 0 " + song_name);
                    databaseReference.child(file_place).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            view_str = dataSnapshot.child(song_name).child("views").getValue(String.class);
                            //artist = dataSnapshot.child(song_name).child("artist").getValue(String.class);
                            imgurl = dataSnapshot.child(song_name).child("imageurl").getValue(String.class);
                            view_count = Integer.valueOf(view_str);
                            Log.d(TAG, "check: 1 " + view_str);
                            Log.d(TAG, "check: 2 " + String.valueOf(view_count));
                            progressBar.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "onDataChange: artist 1 "+artist_view_str);

                    if (artist != null && song_name != null){
                        databaseReference_artist.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                artist_view_str = dataSnapshot.child(artist).child("view_count").getValue(String.class);
                                Log.d(TAG, "onDataChange: artist 2 "+artist_view_str);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Log.d(TAG, "onDataChange: artist 3 "+artist_view_str);

                        databaseReference_artist_song.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                song_artist_view = dataSnapshot.child(artist).child(song_name).child("views").getValue(String.class);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    Log.d(TAG, "onDataChange: artist 3 "+artist_view_str);
                    Log.d(TAG, "onDataChange: artist 4 "+artist_view_str);


                    storageReference.child(song_name).getDownloadUrl().addOnSuccessListener(
                            new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    musicLink = uri.toString();
                                    Log.d(TAG, "onSuccess: " + musicLink);
                                    Log.d(TAG, "check: 3 " + String.valueOf(view_count));
                                    view_count = view_count + 1;
                                    Log.d(TAG, "check: 4 " + String.valueOf(view_count));
                                    view_str = String.valueOf(view_count);
                                    Log.d(TAG, "check: 5 " + view_str);
                                    databaseReference.child(file_place).child(song_name).child("views").setValue(view_str);
                                    if (artist_view_str != null && song_artist_view != null) {
                                        Log.d(TAG, "onSuccess: artist view " + artist_view);
                                        song_artist_view_count = Integer.valueOf(song_artist_view);
                                        Log.d(TAG, "onDataChange: artist 5 " + artist_view_str);
                                        artist_view = Integer.valueOf(artist_view_str);
                                        databaseReference_artist_song.child(artist).child(song_name).child("views").setValue(String.valueOf(song_artist_view_count + 1));
                                        databaseReference_artist.child(artist).child("view_count").setValue(String.valueOf(artist_view + 1));
                                    }
                                    //Log.d(TAG, "check: 6 "+songList.get(position).getSname());
                                    Log.d(TAG, "onSuccess: artist name "+artist);
                                    Log.d(TAG, "check: 6 " + view_str);
                                    intent.putExtra("artist_view",artist_view_str);
                                    intent.putExtra("artist_song_view",song_artist_view);
                                    intent.putExtra("view", view_str);
                                    intent.putExtra("song_url", musicLink);
                                    intent.putExtra("type",file_type);
                                    intent.putExtra("music_image_url",imgurl);
                                    intent.putExtra("file_place",file_place);
                                    intent.putExtra("return_place_home",return_place);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        Log.d(TAG, "onDataChange: artist 6 "+artist_view_str);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // clicked item
                    if (position_item == -1){
                        position_item = position;
                    }
                    else {
                        return;
                    }
                    song_name = songList.get(position).getSname();
                    artist = songList.get(position).getArtist();
                    intent.putExtra("song_name", song_name);
                    Log.d(TAG, "onItemClick: position" + songList.get(position).toString());
                    Log.d(TAG, "check: 0 " + song_name);
                    Log.d(TAG, "check: 0 artist " + artist);

                    databaseReference.child(file_place).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //artist = dataSnapshot.child(song_name).child("artist").getValue(String.class);
                            view_str = dataSnapshot.child(song_name).child("views").getValue(String.class);
                            imgurl = dataSnapshot.child(song_name).child("imageurl").getValue(String.class);
                            view_count = Integer.valueOf(view_str);

                            Log.d(TAG, "check: 1 " + view_str);
                            Log.d(TAG, "check: 2 " + String.valueOf(view_count));
                            progressBar.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "onDataChange: artist 1 "+artist_view_str);

                    if (artist != null && song_name != null){
                        databaseReference_artist.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                artist_view_str = dataSnapshot.child(artist).child("view_count").getValue(String.class);
                                Log.d(TAG, "onDataChange: artist 2 "+artist_view_str);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Log.d(TAG, "onDataChange: artist 3 "+artist_view_str);

                        databaseReference_artist_song.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                song_artist_view = dataSnapshot.child(artist).child(song_name).child("views").getValue(String.class);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    Log.d(TAG, "onStart: artist views2 "+artist_view_str);
                    Log.d(TAG, "onDataChange: artist 3 "+artist_view_str);
                    Log.d(TAG, "onDataChange: artist 4 "+artist_view_str);


                    storageReference.child(song_name).getDownloadUrl().addOnSuccessListener(
                            new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    musicLink = uri.toString();
                                    Log.d(TAG, "onSuccess: " + musicLink);
                                    Log.d(TAG, "check: 3 " + String.valueOf(view_count));
                                    view_count = view_count + 1;
                                    Log.d(TAG, "check: 4 " + String.valueOf(view_count));
                                    view_str = String.valueOf(view_count);
                                    Log.d(TAG, "check: 5 " + view_str);
                                    databaseReference.child(file_place).child(song_name).child("views").setValue(view_str);
                                    if (artist_view_str != null && song_artist_view != null) {
                                        Log.d(TAG, "onSuccess: artist view " + artist_view);
                                        song_artist_view_count = Integer.valueOf(song_artist_view);
                                        Log.d(TAG, "onDataChange: artist 5 " + artist_view_str);
                                        artist_view = Integer.valueOf(artist_view_str);
                                        databaseReference_artist_song.child(artist).child(song_name).child("views").setValue(String.valueOf(song_artist_view_count + 1));
                                        databaseReference_artist.child(artist).child("view_count").setValue(String.valueOf(artist_view + 1));
                                    }
                                    //Log.d(TAG, "check: 6 "+songList.get(position).getSname());
                                    Log.d(TAG, "check: 6 " + view_str);
                                    intent.putExtra("view", view_str);
                                    intent.putExtra("song_url", musicLink);
                                    intent.putExtra("type",file_type);
                                    intent.putExtra("music_image_url",imgurl);
                                    intent.putExtra("file_place",file_place);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
            if (auth.getCurrentUser() != null) {
                databaseadmin.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("admin")){
                            imageView1.setVisibility(View.VISIBLE);
                        }
                        else {
                            imageView1.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Log.d(TAG, "onDataChange: admin2 " +admin);

            }
            else {
                imageView1.setVisibility(View.GONE);
            }
        Log.d(TAG, "onDataChange: admin4 " +admin);
        Log.d(TAG, "onDataChange: admin5 " +admin);

    }


    public void upload(View view){
        Intent intent = new Intent(this,Upload.class);
        startActivity(intent);
    }


}
