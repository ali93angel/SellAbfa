package com.app.leon.sellabfa.Models.Enums;

/**
 * Created by Leon on 1/10/2018.
 */

public enum SharedReferenceNames {
    ESHTERAK_OR_QERAAT("com.app.leon.sellabfa.eshterak_or_qeraat"),
    ACCOUNT("com.app.leon.sellabfa.account_info");

    private final String value;

    SharedReferenceNames(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
