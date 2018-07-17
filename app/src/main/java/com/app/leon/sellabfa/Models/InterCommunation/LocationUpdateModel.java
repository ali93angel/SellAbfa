package com.app.leon.sellabfa.Models.InterCommunation;

/**
 * Created by Leon on 1/28/2018.
 */

public class LocationUpdateModel {
    double latitude;
    double longitude;
    int gisAccuracy;

    public LocationUpdateModel(android.location.Location lastLocation, int gisAccuracy) {
        this.latitude = lastLocation.getLatitude();
        this.longitude = lastLocation.getLongitude();
        this.gisAccuracy = gisAccuracy;
    }
}
