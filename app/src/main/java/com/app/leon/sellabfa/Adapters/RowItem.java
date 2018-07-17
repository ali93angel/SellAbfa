package com.app.leon.sellabfa.Adapters;

/**
 * Created by Leon on 2/17/2018.
 */

public class RowItem {
    private String Title;

    public RowItem(String Title) {
        this.Title = Title;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    @Override
    public String toString() {
        return Title;
    }
}
