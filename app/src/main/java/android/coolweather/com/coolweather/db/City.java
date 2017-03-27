package android.coolweather.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 75213 on 2017/3/27.
 * 城市数据库
 * cityName记录城市的名字
 * cityCode记录城市的代号
 * provinceId记录当前城市属于哪一个省会
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }


}
