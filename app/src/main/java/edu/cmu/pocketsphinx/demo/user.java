package edu.cmu.pocketsphinx.demo;

public class user {


    public String email;
    public String username;
    public String full_name;
    public String ref_number;
    public String gender;
    public  String status;



    public user() {

    }

    public user(String email, String username, String full_name, String ref_number, String gender,String status) {
        this.email = email;
        this.username = username;
        this.full_name = full_name;
        this.ref_number = ref_number;
        this.gender = gender;
        this.status= status;
    }

}
