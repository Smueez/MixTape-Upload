package com.example.user.mixtapeupload;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class VideoList extends AppCompatActivity {
    String view_str;
    Uri filePath;
    StorageReference storageReference;
    DatabaseReference databaseReference,databaseReferencelist,databaseadmin;
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
    //GridView gridView;
    String file_type = "video",muser_str,admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            String muser_str1 = auth.getCurrentUser().getEmail();
            muser_str = muser_str1.replace(".com","");
            Log.d(TAG, "onCreate: " + muser_str);
            databaseadmin = FirebaseDatabase.getInstance().getReference().child("profile").child(muser_str).child("admin");

        }
        storageReference = FirebaseStorage.getInstance().getReference().child(file_type);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(file_type);
        listView = findViewById(R.id.listview1);
        songList = new ArrayList<>();
        databaseReferencelist = FirebaseDatabase.getInstance().getReference().child(file_type);
        intent = new Intent(this, VideoPlayer.class);
        progressBar = findViewById(R.id.progressBar3);
        mauth = FirebaseAuth.getInstance();
        intent1 = new Intent(this,MainActivity.class);
        new_bttn = findViewById(R.id.newbttn);
        all_bttn = findViewById(R.id.all);
        hot_bttn = findViewById(R.id.hot);
        topViewbttn = findViewById(R.id.viewed);
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
                Listadapter listadapter = new Listadapter(VideoList.this,songList);
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


    }
    @Override
    public void onBackPressed() {
       Intent intent1 = new Intent(this,home.class);
       startActivity(intent1);

    }

}
