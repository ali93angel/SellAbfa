package com.app.leon.sellabfa.Models.ViewModels;

import java.math.BigDecimal;

public class LatLang {
    public BigDecimal Latitude;
    public BigDecimal longtitude;

    public LatLang() {
    }

    public LatLang(double latitude, double longtitude) {
        this.Latitude = new BigDecimal(latitude);
        this.longtitude = new BigDecimal(longtitude);
    }
}
