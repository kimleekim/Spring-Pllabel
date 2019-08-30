package org.webapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Overall {

    private String station;
    private String restaurants;
    private int instaCNT;
    private int youtubeCNT;
    private long likeCNT;

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Overall(String station, String restaurants, int instaCNT, int youtubeCNT, long likeCNT) {
        this.station = station;
        this.restaurants = restaurants;
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getRestaurants() {
        return restaurants;
    }

    public List<String> getRestaurantsOfJson() {
        String[] tempArray;
        List<String> toJavaObject;

        try {
            String trimmedJson;
            trimmedJson = this.restaurants.substring(5, restaurants.length() - 3)
                    .replace("\\", "")
                    .replace("\"\"", "\"");

            System.out.println("다듬어진 스트링 : " + trimmedJson);
            tempArray = gson.fromJson(trimmedJson, String[].class);
            toJavaObject = new ArrayList<String>(Arrays.asList(tempArray));

            return toJavaObject;

        } catch(StringIndexOutOfBoundsException
                | IllegalStateException
                | JsonSyntaxException e) {

            tempArray = gson.fromJson(getRestaurants(), String[].class);
            toJavaObject = new ArrayList<>(Arrays.asList(tempArray));

            return toJavaObject;
        }
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
