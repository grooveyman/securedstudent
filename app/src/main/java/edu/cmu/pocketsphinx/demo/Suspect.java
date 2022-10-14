package edu.cmu.pocketsphinx.demo;

public class Suspect {

    private String Suspect_Name, Suspect_Details, image;

    public Suspect(){

    }

    public Suspect(String suspect_Name, String suspect_Details, String image) {
        Suspect_Name = suspect_Name;
        Suspect_Details = suspect_Details;
        this.image = image;
    }

    public String getSuspect_Name() {
        return Suspect_Name;
    }

    public void setSuspect_Name(String duspect_Name) {
        Suspect_Name = duspect_Name;
    }

    public String getSuspect_Details() {
        return Suspect_Details;
    }

    public void setSuspect_Details(String suspect_Details) {
        Suspect_Details = suspect_Details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
