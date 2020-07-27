package com.example.user.mixtapeupload;
public class Artist_class {

    String artist_name;
    String song_count;
    String img_url;
    String total_likes;
    String view_count;

    Artist_class(){

    }
    Artist_class(String artist_name,String img_url,String song_count,String total_likes,String view_count){
        this.artist_name = artist_name;
        this.img_url = img_url;
        this.song_count = song_count;
        this.total_likes = total_likes;
        this.view_count = view_count;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getSong_count() {
        return song_count;
    }

    public String getTotal_likes() {
        return total_likes;
    }

    public String getView_count() {
        return view_count;
    }
}
