package org.webapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

public class Instaplace {
    private long key;
    private String station;
    private String post;
    private Date date;
    private String hashtag;

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Instaplace(long key, String station, String post, Date date) {
        this.key = key;
        this.station = station;
        this.post = post;
        this.date = date;
    }

    public Instaplace() {}

    public long getKey() {
        return key;
    }

    public String getStation() {
        return station;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPost() {
        return post;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setHashtag(List<String> hashtag) {
        try {
            this.hashtag = gson.toJson(hashtag);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getHashtag() {
        return hashtag;
    }

    public List<String> getHashtagOfJson() {
        String[] tempArray = gson.fromJson(this.hashtag, String[].class);
        List<String> toJavaObject = Arrays.asList(tempArray);

        return toJavaObject;
    }
}
