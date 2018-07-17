package com.app.leon.sellabfa.Models.Enums;

public enum OffloadStateEnum {
    INSERTED(1),
    REGISTERED(2),
    SENT(4),
    ARCHIVED(8),
    LOGICAL_DELETED(16),
    DELETED(32),
    SENT_WITH_ERROR(64);

    private final int value;

    OffloadStateEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
