package edu.cmu.pocketsphinx.demo;

public class alert {
    public String username,full_name,ref_number,gender,user_uid,message,latitude,longitude;

    public alert(){

    }

    public alert(String username, String full_name, String ref_number, String gender,String user_uid,String message,String latitude,String longitude) {
        this.username = username;
        this.full_name = full_name;
        this.ref_number = ref_number;
        this.gender = gender;
        this.user_uid = user_uid;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
