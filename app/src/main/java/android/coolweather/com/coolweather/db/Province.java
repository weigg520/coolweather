package android.coolweather.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 75213 on 2017/3/27.
 * 省会的数据库
 * id:是每一个实体类都应该有的字段
 * provinceName：记录省会的名字
 * provinceCode:记录省会的代号
 * LitePal的每一个实体类都需要继承DataSupport类
 */

public class Province extends DataSupport{
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }


}
