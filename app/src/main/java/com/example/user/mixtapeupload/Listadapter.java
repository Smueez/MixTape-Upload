package com.example.user.mixtapeupload;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Listadapter extends ArrayAdapter<Song> {
    private Activity context;
    private List<Song> list;

    public Listadapter(Activity context,List<Song>list){
        super(context,R.layout.list_song,list);

        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View mylistview = inflater.inflate(R.layout.list_song,null,true);



        TextView songName = mylistview.findViewById(R.id.song);
        TextView artist = mylistview.findViewById(R.id.artist);

        TextView likes = mylistview.findViewById(R.id.likes);
        TextView views = mylistview.findViewById(R.id.views);

        Song song1 = list.get(position);
        songName.setText(song1.getSname());
        artist.setText(song1.getArtist());
        likes.setText(song1.getLikes());
        views.setText(song1.getViews());

        return mylistview;
    }
}
