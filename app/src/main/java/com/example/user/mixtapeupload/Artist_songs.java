package com.example.user.mixtapeupload;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Artist_songs extends AppCompatActivity {

    TextView artist_name;
    ListView listView;
    String TAG="artist song activity",song_name,artist_name_str;
    DatabaseReference databaseReference_artist,databaseReference_songs,databaseReference_artist_song;
    StorageReference storageReference;
    List<Song> songList1;
    String view_str,imgurl,file_place,musicLink,artist_view_str,song_artist_view;
    Intent intent;
    RelativeLayout listview_layout;
    int view_count;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);
        progressBar = findViewById(R.id.progressBar4);
        artist_name = findViewById(R.id.textView22);
        listView = findViewById(R.id.listview_artist_song);
        artist_name.setText(getIntent().getExtras().getString("artist_name_song"));
        artist_view_str = artist_name.getText().toString().trim();
        databaseReference_artist = FirebaseDatabase.getInstance().getReference();
        songList1 = new ArrayList<>();
        intent = new Intent(this,Music_player.class);
        storageReference = FirebaseStorage.getInstance().getReference().child("audio");
        databaseReference_songs = FirebaseDatabase.getInstance().getReference().child("audio");
        databaseReference_artist_song = FirebaseDatabase.getInstance().getReference().child("artist_songs");
        file_place="All Music";
    }
    public int sCompare(String str1, String str2)
    {
        int res = str1.compareTo(str2);
        if(res > 0) return 1;
        else return -1;
    }
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference_artist.child("artist_songs").child(artist_name.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listView.setAdapter(null);
                songList1.clear();
                Log.d(TAG, "onDataChange artist name: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    songList1.add(song);
                }
                Collections.sort(songList1, new Comparator<Song>() {
                    public int compare(Song s1, Song s2) {
                        int res =  sCompare(s1.sname,s2.sname);
                        return res;
                    }
                });
                Listadapter listadapter = new Listadapter(Artist_songs.this, songList1);
                listView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clicked item
                song_name = songList1.get(position).getSname();
                Log.d(TAG, "onItemClick: song name "+song_name);
                artist_name_str = songList1.get(position).getArtist();
                intent.putExtra("song_name", song_name);

                progressBar.setVisibility(View.VISIBLE);

                Log.d(TAG, "onItemClick: position" + songList1.get(position).toString());
                Log.d(TAG, "check: 0 " + song_name);
                databaseReference_songs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("All Music").hasChild(song_name)){
                            file_place = "All Music";
                            Log.d(TAG, "onDataChange1: "+file_place);
                            databaseReference_songs.child(file_place).child(song_name).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    view_str = dataSnapshot.child("views").getValue(String.class);
                                    view_count = Integer.valueOf(view_str);
                                    imgurl = dataSnapshot.child("imageurl").getValue(String.class);
                                    //file_place = dataSnapshot.child("fileplace").getValue(String.class);


                                    Log.d(TAG, "check: 1 " + view_str);
                                    Log.d(TAG, "check: 2 " + String.valueOf(view_count));
                                    //progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else if (dataSnapshot.child("New Music").hasChild(song_name)){
                            file_place = "New Music";
                            Log.d(TAG, "onDataChange2: "+file_place);
                            databaseReference_songs.child(file_place).child(song_name).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    view_str = dataSnapshot.child("views").getValue(String.class);
                                    view_count = Integer.valueOf(view_str);
                                    imgurl = dataSnapshot.child("imageurl").getValue(String.class);
                                    //file_place = dataSnapshot.child("fileplace").getValue(String.class);


                                    Log.d(TAG, "check: 1 " + view_str);
                                    Log.d(TAG, "check: 2 " + String.valueOf(view_count));
                                    //progressBar.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else if (dataSnapshot.child("Who's Hot").hasChild(song_name)){
                            file_place = "Who's Hot";
                            Log.d(TAG, "onDataChange3: "+file_place);
                            databaseReference_songs.child(file_place).child(song_name).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    view_str = dataSnapshot.child("views").getValue(String.class);
                                    view_count = Integer.valueOf(view_str);
                                    imgurl = dataSnapshot.child("imageurl").getValue(String.class);
                                    //file_place = dataSnapshot.child("fileplace").getValue(String.class);


                                    Log.d(TAG, "check: 1 " + view_str);
                                    Log.d(TAG, "check: 2 " + String.valueOf(view_count));
                                    //progressBar.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        Log.d(TAG, "onDataChange5: "+file_place);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });//take if statement
                Log.d(TAG, "onDataChange4: file place "+file_place);

                if (song_name != null){
                    databaseReference_artist.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            artist_view_str = dataSnapshot.child(artist_name_str).child("view_count").getValue(String.class);
                            Log.d(TAG, "onDataChange: artist 2 "+artist_view_str);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    databaseReference_artist_song.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            song_artist_view = dataSnapshot.child(artist_name_str).child(song_name).child("views").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
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
                                Log.d(TAG, "check: 5 view " + view_str);
                                Log.d(TAG, "onSuccess: file_place "+file_place);
                                Log.d(TAG, "onSuccess: song_name " +song_name);
                                databaseReference_songs.child(file_place).child(song_name).child("views").setValue(view_str);
                                //Log.d(TAG, "check: 6 "+songList.get(position).getSname());
                                Log.d(TAG, "check: 6 " + view_str);
                                intent.putExtra("view", view_str);
                                intent.putExtra("song_url", musicLink);
                                intent.putExtra("type",file_place);
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
    }
}
