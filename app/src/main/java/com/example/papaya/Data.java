package com.example.papaya;

import java.io.Serializable;

class Data implements Serializable {

    String Id;
    String Date;
    String Text;
    String Gps;
    String Id_Photo;
    String Url_Photo;


    public String getId(){
        return Id;
    }

    public String getDate(){
        return Date;
    }
    public String getText(){
        return Text;
    }
    public String getGps(){
        return Gps;
    }
    public String getId_Photo(){
        return Id_Photo;
    }
    public String getUrl_Photo(){
        return Url_Photo;
    }


    public void setId(String Id){
        this.Id = Id;
    }

    public void setDate(String Date){
        this.Date = Date;
    }
    public void setText(String Text){
        this.Text = Text;
    }
    public void setGps(String Gps)  {this.Gps = Gps; }
    public void setId_Photo(String Id_Photo){
        this.Id_Photo = Id_Photo;
    }
    public void setUrl_Photo(String Url_Photo){
        this.Url_Photo = Url_Photo;
    }
}