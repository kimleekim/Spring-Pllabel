package org.webapp.model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

public class Instafood {

    private long key;
    private String station;
    private String post;
    private Date date;
    private long likeCNT;
    private String myRestaurant;
    private String photoURL;

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Instafood (long key, String station, String post, Date date, long likeCNT, String photoURL) {
        this.key = key;
        this.station = station;
        this.post = post;
        this.date = date;
        this.likeCNT = likeCNT;
        this.photoURL = photoURL;
    }

    public Instafood () {
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLikeCNT(long likeCNT) {
        this.likeCNT = likeCNT;
    }

    public long getLikeCNT() {
        return likeCNT;
    }

    public void setMyRestaurant(List<String> myRestaurants) {
        try {
            this.myRestaurant = gson.toJson(myRestaurants);
            //db에 넣을때는 string으로 넣어야됨
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getMyRestaurant() {
        return myRestaurant;
    }

    public List<String> getMyRestaurantOfJson() {
        String[] tempArray = gson.fromJson(this.myRestaurant, String[].class);
        List<String> toJavaObject = Arrays.asList(tempArray);

        return toJavaObject;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }


}
