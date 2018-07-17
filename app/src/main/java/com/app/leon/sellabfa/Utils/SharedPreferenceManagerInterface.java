package com.app.leon.sellabfa.Utils;

public interface SharedPreferenceManagerInterface {
    void putData(String key, String value);

    void putData(String key, int value);

    void putData(String key, boolean value);

    String getStringData(String key);

    int getIntData(String key);

    boolean getBoolData(String key);
}
