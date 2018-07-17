package com.app.leon.sellabfa.Models.Enums;

/**
 * Created by Leon on 1/10/2018.
 */

public enum BundleEnum {
    BILL_ID("bill_Id"),
    TRACK_NUMBER("trackNumber"),
    DATA("data"),
    READ_STATUS("readStatus"),
    THEME("theme"),
    ACCOUNT("ACCOUNT"),
    TYPE("type"),
    ON_OFFLOAD("ON_OFFLOAD"),
    POSITION("position"),
    SPINNER_POSITION("spinner_position"),
    COUNTER_STATE_POSITION("counterStatePosition"),
    COUNTER_STATE_CODE("counterStatePosition"),
    NUMBER("counterStateCode"),
    ESHTERAK_OR_QERAAT("eshterak_or_qeraat"),
    CURRENT_PAGE("number");

    private final String value;

    BundleEnum(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
