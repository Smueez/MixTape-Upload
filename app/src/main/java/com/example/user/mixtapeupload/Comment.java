package com.example.user.mixtapeupload;

public class Comment {
    String comment_text,comment_name;

    Comment(){

    }
    Comment(String comment_name, String comment_text){
        this.comment_name = comment_name;
        this.comment_text = comment_text;
    }

    public String getComment_name() {
        return comment_name;
    }

    public String getComment_text() {
        return comment_text;
    }
}
