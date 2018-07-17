package com.app.leon.sellabfa.Utils;

/**
 * Created by Leon on 1/10/2018.
 */

public interface ISharedPreferenceManager {
    <T1, T2> T2 get(T1 key);

    <T> void put(T key, T value);

    void apply();

    boolean CheckIsNotEmpty(String key);
}
