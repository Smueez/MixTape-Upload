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
import android.widget.RelativeLayout;
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

public class Artist_list extends AppCompatActivity {

    DatabaseReference databaseReference_artist,databaseReference_songs;
    StorageReference storageReference;
    GridView gridView;
    ListView listView;
    List<Artist_class> songList;
    String TAG = "artist activity",artist_name,file_place;
    Intent intent;
    RelativeLayout listview_layout,gridview_layout;
    int view_count;
    public boolean cliked = false;
    Intent intent_artist_song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);

        databaseReference_artist = FirebaseDatabase.getInstance().getReference();
        databaseReference_songs = FirebaseDatabase.getInstance().getReference().child("artist_songs");
        gridView = findViewById(R.id.gridview_artis);
        songList = new ArrayList<>();

        intent = new Intent(this,Music_player.class);
        listview_layout = findViewById(R.id.listview_layout);
        gridview_layout = findViewById(R.id.artist_list);

        intent_artist_song = new Intent(this,Artist_songs.class);

    }
    public int sCompare(String str1, String str2)
    {
        int res = str1.compareTo(str2);
        if(res > 0) return 1;
        else return -1;
    }
    int position_item = -1;
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference_artist.child("artist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gridView.setAdapter(null);
                songList.clear();
                Log.d(TAG, "onDataChange artist name: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Artist_class song = ds.getValue(Artist_class.class);
                    songList.add(song);
                }
                Collections.sort(songList, new Comparator<Artist_class>() {
                    public int compare(Artist_class s1, Artist_class s2) {
                        int res = sCompare(s1.artist_name,s2.artist_name);
                        return res;
                    }
                });
                Artist_lis_adapter listadapter = new Artist_lis_adapter(Artist_list.this,songList);
                gridView.setAdapter(listadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                cliked = true;
                artist_name = songList.get(position).getArtist_name();
                //intent.putExtra("song_name", artist_name);
                Log.d(TAG, "onItemClick: position" + songList.get(position).toString());
                Log.d(TAG, "check: 0 " + artist_name);

                intent_artist_song.putExtra("artist_name_song",artist_name);
                startActivity(intent_artist_song);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

            Intent intent1 = new Intent(this, home.class);
            startActivity(intent1);

    }
}
