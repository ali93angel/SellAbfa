package com.app.leon.sellabfa.Infrastructure;

/**
 * Created by Leon on 1/6/2018.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.orm.SchemaGenerator;
import com.orm.SugarApp;
import com.orm.SugarContext;
import com.orm.SugarDb;

public class MyApplication extends SugarApp {
    public static Context getAppContext() {
        return MyApplication.getAppContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(getApplicationContext());
//        SugarContext.init(getAppContext());
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
//        new test();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

//    class test extends MultiDexApplication {
//        public test() {
//            MultiDex.install(this);
//        }
//    }
}
