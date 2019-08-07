package org.webapp.model;
import java.sql.Date;

public class Instafood {

    private long key;
    private String station;
    private String post;
    private String photoURL;
    private Date date;

    public Instafood (long key, String station, String post, String photoURL, Date date) {
        this.key = key;
        this.station = station;
        this.post = post;
        this.photoURL = photoURL;
        this.date = date;
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

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
