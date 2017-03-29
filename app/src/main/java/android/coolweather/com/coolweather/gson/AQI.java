package android.coolweather.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 75213 on 2017/3/28.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        @SerializedName("aqi")
        public String api;
        @SerializedName("pm25")
        public String pm25;
    }
}
