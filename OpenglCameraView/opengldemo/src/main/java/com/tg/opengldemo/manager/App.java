package com.tg.opengldemo.manager;

import android.app.Application;
import android.util.DisplayMetrics;

/**
 * @CreadBy ：DramaScript
 * @date 2017/8/17
 */
public class App extends Application {

    public static App CONTEXT;
    public static int mScreenWidth = 0;
    public static int mScreenHeight = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;

        // DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        DisplayMetrics mDisplayMetrics = getApplicationContext().getResources()
                .getDisplayMetrics();
        App.mScreenWidth = mDisplayMetrics.widthPixels;
        App.mScreenHeight = mDisplayMetrics.heightPixels;
    }
}
