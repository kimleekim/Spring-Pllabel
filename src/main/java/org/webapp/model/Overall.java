package org.webapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class Overall {

    private String station;
    private String restaurants;
    private int instaCNT;
    private int youtubeCNT;
    private long likeCNT;

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Overall(String station, int instaCNT, int youtubeCNT, long likeCNT) {
        this.station = station;
        this.instaCNT = instaCNT;
        this.youtubeCNT = youtubeCNT;
        this.likeCNT = likeCNT;
    }

    public Overall() {}

    public void setStation(String station) {
        this.station = station;
    }

    public String getStation() {
        return station;
    }

    public void setRestaurants(List<String> restaurants) {
        try {
            this.restaurants = gson.toJson(restaurants);
            //db에 넣을때는 string으로 넣어야됨
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getHashtag() {
        return restaurants;
    }

    public List<String> getHashtagOfJson() {
        String[] tempArray = gson.fromJson(this.restaurants, String[].class);

        return Arrays.asList(tempArray);
    }

    public void setInstaCNT(int instaCNT) {
        this.instaCNT = instaCNT;
    }

    public int getInstaCNT() {
        return instaCNT;
    }

    public void setYoutubeCNT(int youtubeCNT) {
        this.youtubeCNT = youtubeCNT;
    }

    public int getYoutubeCNT() {
        return youtubeCNT;
    }

    public void setLikeCNT(int likeCNT) {
        this.likeCNT = likeCNT;
    }

    public long getLikeCNT() {
        return likeCNT;
    }
}
