package com.app.leon.sellabfa.Models.ViewModels;

/**
 * Created by Leon on 1/19/2018.
 */


public class SpinnerDataModel {

    private final String text;
    private final Integer imageId;

    public SpinnerDataModel(String text, Integer imageId) {
        this.text = text;
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public Integer getImageId() {
        return imageId;
    }
}