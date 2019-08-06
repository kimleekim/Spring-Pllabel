package org.webapp.model;

public class Overall {

    private String station;
    private int instaCNT;
    private int youtubeCNT;
    private long likeCNT;

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
