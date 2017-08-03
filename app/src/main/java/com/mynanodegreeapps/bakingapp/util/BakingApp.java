package com.mynanodegreeapps.bakingapp.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by inbkumar01 on 8/2/2017.
 */

public class BakingApp extends Application {
    private static BakingApp instance;

    public static BakingApp getInstance(){
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate(){
        instance = this;
        super.onCreate();
    }
}
