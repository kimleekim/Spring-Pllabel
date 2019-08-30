package org.webapp.model;

import java.sql.Date;

public class Youtubehot {
    private long key;
    private String station;
    private String title;
    private String content;
    private long totalview;
    private String creator;
    private Date date;
    private String thumbnailURL;
    private String videoLink;

    public Youtubehot (long key, String station, String title, String content, long totalview, String creator, Date date, String thumbnailURL, String videoLink) {
        this.key = key;
        this.station = station;
        this.title = title;
        this.content = content;
        this.totalview = totalview;
        this.creator = creator;
        this.date = date;
        this.thumbnailURL = thumbnailURL;
        this.videoLink = videoLink;
    }

    public Youtubehot () {

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public long getTotalview() { return totalview; }

    public void setTotalview(long totalview) { this.totalview = totalview; }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }
}