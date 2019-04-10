package com.example.user.mixtapeupload;

public class PassDataProfile {
    String password,name,imageuri,sex,admin;
    PassDataProfile(){

    }
    PassDataProfile(String name,String password, String imageuri, String sex){
        this.name = name;
        this.password = password;
        this.imageuri = imageuri;
        this.sex = sex;
        this.admin = "false";
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getAdmin() {
        return admin;
    }

    public String getImageuri() {
        return imageuri;
    }

    public String getSex() {
        return sex;
    }
}
