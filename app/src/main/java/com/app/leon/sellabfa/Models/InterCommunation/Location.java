package com.app.leon.sellabfa.Models.InterCommunation;

public class Location {
    int d1, d2, l1, l2;
    Double lat, lon;
    String id;

    public Location(String id, int d1, int d2, int l1, int l2, Double lat, Double lon) {
        this.d1 = d1;
        this.d2 = d2;
        this.l1 = l1;
        this.l2 = l2;
        this.lat = lat;
        this.lon = lon;
        this.id = id;
    }
}
