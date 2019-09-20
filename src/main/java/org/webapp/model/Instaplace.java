package org.webapp.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.codehaus.jettison.json.JSONArray;
import org.openqa.selenium.json.Json;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Instaplace {
    private long key;
    private String station;
    private String post;
    private long likeCNT;
    private Date date;
    private String hashtag;
    private String description;

    private final static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public Instaplace(long key, String station, String post, long likeCNT, Date date, String hashtag, String description) {
        this.key = key;
        this.station = station;
        this.post = post;
        this.likeCNT = likeCNT;
        this.date = date;
        this.hashtag = hashtag;
        this.description = description;
    }

    public Instaplace() {}

    public long getKey() {
        return key;
    }

    public void setStation(String station) {
        this.station = station;
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

    public void setLikeCNT(long likeCNT) {
        this.likeCNT = likeCNT;
    }

    public long getLikeCNT() {
        return likeCNT;
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
        String[] tempArray;
        List<String> toJavaObject;

        try {
            String trimmedJson;
            trimmedJson = this.hashtag.substring(5, hashtag.length() - 3)
                    .replace("\\", "")
                    .replace("\"\"", "\"");

            tempArray = gson.fromJson(trimmedJson, String[].class);
            toJavaObject = new ArrayList<String>(Arrays.asList(tempArray));

            return toJavaObject;

        } catch(StringIndexOutOfBoundsException
                | IllegalStateException
                | JsonSyntaxException
                | NullPointerException e) {

            tempArray = gson.fromJson(getHashtag(), String[].class);

            if(tempArray == null) {
                tempArray = new String[0];
            }
            toJavaObject = new ArrayList<>(Arrays.asList(tempArray));

            return toJavaObject;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
