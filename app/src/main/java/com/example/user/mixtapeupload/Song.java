package com.example.user.mixtapeupload;

public class Song {
    public String artist,sname;
    public String views;
    public String likes;
    public String url;
    public String sec,min;
    public long day,mounth,year;
    public  String imageurl;
    Song(){

    }
    Song(String sec,String min,String url,String imageurl, String artist, String likes, String views, String sname, long day, long mounth, long year){
        this.url = url;
        this.artist = artist;
        this.likes = likes;
        this.views = views;
        this.sec = sec;
        this.min = min;
        this.sname = sname;
        this.day = day;
        this.mounth = mounth;
        this.year = year;
        this.imageurl = imageurl;
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

    public long getDay() {
        return day;
    }

    public long getMounth() {
        return mounth;
    }

    public long getYear() {
        return year;
    }

    public String getImageurl() {
        return imageurl;
    }
}
