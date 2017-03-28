package android.coolweather.com.coolweather;

import android.app.Application;

import com.facebook.stetho.Stetho;

import org.litepal.LitePal;

/**
 * Created by 75213 on 2017/3/27.
 */

public class Myapplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        Stetho.initializeWithDefaults(this);
    }
}
