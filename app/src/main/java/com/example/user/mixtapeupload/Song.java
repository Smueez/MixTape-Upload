package com.example.user.mixtapeupload;

public class Song {
    public String artist,sname;
    public String views;
    public String likes;
    public String url;
    public String sec,min;

    Song(){

    }
    Song(String sec,String min,String url, String artist, String likes, String views, String sname){
        this.url = url;
        this.artist = artist;
        this.likes = likes;
        this.views = views;
        this.sec = sec;
        this.min = min;
        this.sname = sname;
    }


    public String getLikes() {
        return likes;
    }

    public String getViews() {
        return views;
    }

    public String getArtist() {
        return artist;
    }

    public String getMin() {
        return min;
    }

    public String getSec() {
        return sec;
    }

    public String getSname() {
        return sname;
    }
}
