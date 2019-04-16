package com.example.user.mixtapeupload;


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

import android.widget.AdapterView;
import android.widget.Button;
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

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String view_str;
    Uri filePath;
   StorageReference storageReference;
    private static final int SELECT_AUDIO = 2;
    DatabaseReference databaseReference,databaseReferencelist;
    ListView listView;
    List<Song> songList;
    String TAG = "home content";
    ImageView imageView;
    String artist,name,song_name,musicLink;
    Intent intent;
    int view_count = 0;
    FirebaseAuth auth;
    ProgressBar progressBar;
    FirebaseAuth mauth;
    Intent intent1;
    Button hot_bttn,new_bttn,topViewbttn,all_bttn;
    GridView gridView;
    String file_type = "audio";
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
        storageReference = FirebaseStorage.getInstance().getReference().child(file_type);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(file_type);
        listView = findViewById(R.id.listview);
        gridView = findViewById(R.id.gridview);
        songList = new ArrayList<>();
        databaseReferencelist = FirebaseDatabase.getInstance().getReference().child(file_type);
        if (file_type == "audio") {
            intent = new Intent(this, Music_player.class);
        } else {
            intent = new Intent(this, VideoPlayer.class);

        }
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        mauth = FirebaseAuth.getInstance();
        intent1 = new Intent(this,MainActivity.class);
        new_bttn = findViewById(R.id.newbttn);
        all_bttn = findViewById(R.id.all);
        hot_bttn = findViewById(R.id.hot);
        topViewbttn = findViewById(R.id.viewed);
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
            if (file_type == "audio"){
                item.setTitle("Music");
                file_type = "video";
            }
            else  if (file_type == "video"){
                item.setTitle("Videos");
                file_type = "audio";
            }
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
        databaseReferencelist.addValueEventListener(new ValueEventListener() {
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
        databaseReferencelist.addValueEventListener(new ValueEventListener() {
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
        databaseReferencelist.addValueEventListener(new ValueEventListener() {
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
                        int res = sCompare(s1.sname,s2.sname);
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
        databaseReferencelist.addValueEventListener(new ValueEventListener() {
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

    @Override
    protected void onStart() {
        super.onStart();
        databaseReferencelist.addValueEventListener(new ValueEventListener() {
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
                        int res = sCompare(s1.sname,s2.sname);
                        return res;
                    }
                });
                Listadapter listadapter = new Listadapter(home.this,songList);
                gridView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // clicked item
                    song_name = songList.get(position).getSname();
                    intent.putExtra("song_name", song_name);
                    Log.d(TAG, "onItemClick: position" + songList.get(position).toString());
                    Log.d(TAG, "check: 0 " + song_name);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            view_str = dataSnapshot.child(song_name).child("views").getValue(String.class);

                            view_count = Integer.valueOf(view_str);

                            Log.d(TAG, "check: 1 " + view_str);
                            Log.d(TAG, "check: 2 " + String.valueOf(view_count));
                            progressBar.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

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
                                    databaseReference.child(song_name).child("views").setValue(view_str);
                                    //Log.d(TAG, "check: 6 "+songList.get(position).getSname());
                                    Log.d(TAG, "check: 6 " + view_str);
                                    intent.putExtra("view", view_str);
                                    intent.putExtra("song_url", musicLink);
                                    intent.putExtra("type",file_type);
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
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // clicked item
                    song_name = songList.get(position).getSname();
                    intent.putExtra("song_name", song_name);
                    Log.d(TAG, "onItemClick: position" + songList.get(position).toString());
                    Log.d(TAG, "check: 0 " + song_name);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            view_str = dataSnapshot.child(song_name).child("views").getValue(String.class);

                            view_count = Integer.valueOf(view_str);

                            Log.d(TAG, "check: 1 " + view_str);
                            Log.d(TAG, "check: 2 " + String.valueOf(view_count));
                            progressBar.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

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
                                    databaseReference.child(song_name).child("views").setValue(view_str);
                                    //Log.d(TAG, "check: 6 "+songList.get(position).getSname());
                                    Log.d(TAG, "check: 6 " + view_str);
                                    intent.putExtra("view", view_str);
                                    intent.putExtra("song_url", musicLink);
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

    }


    public void upload(View view){
        Intent intent = new Intent(this,Upload.class);
        startActivity(intent);
    }

}
