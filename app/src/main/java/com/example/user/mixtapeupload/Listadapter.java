package com.example.user.mixtapeupload;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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
