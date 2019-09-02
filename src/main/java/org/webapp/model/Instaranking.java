package org.webapp.model;

public class Instaranking {
    private long key;
    private String station;
    private String placetag;
    private long placetagCNT;
    private long likeCNT;

    public Instaranking (long key, String station, String placetag, long placetagCNT, long likeCNT) {
        this.key = key;
        this.station = station;
        this.placetag = placetag;
        this.placetagCNT = placetagCNT;
        this.likeCNT = likeCNT;
    }

    public Instaranking () {
    }

    public long getKey() {
        return key;
    }

    public String getStation() {
        return station;
    }

    public String getPlacetag() {
        return placetag;
    }

    public long getPlacetagCNT() {
        return placetagCNT;
    }

    public long getLikeCNT() {
        return likeCNT;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public void setPlacetag(String placetag) {
        this.placetag = placetag;
    }

    public void setPlacetagCNT(int placetagCNT) {
        this.placetagCNT = placetagCNT;
    }

    public void setLikeCNT(int likeCNT) {
        this.likeCNT = likeCNT;
    }
}
