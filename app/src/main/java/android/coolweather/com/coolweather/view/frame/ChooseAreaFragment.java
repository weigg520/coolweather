package android.coolweather.com.coolweather.view.frame;

import android.app.ProgressDialog;
import android.content.Intent;
import android.coolweather.com.coolweather.R;
import android.coolweather.com.coolweather.db.City;
import android.coolweather.com.coolweather.db.County;
import android.coolweather.com.coolweather.db.Province;
import android.coolweather.com.coolweather.util.HttpUtil;
import android.coolweather.com.coolweather.util.Utility;
import android.coolweather.com.coolweather.view.WeatherActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 75213 on 2017/3/28.
 */

public class ChooseAreaFragment extends Fragment {
    //选中级别标记
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    //界面显示
    private ProgressDialog progressDialog;
    private Button back_button;
    private TextView title_text;
    private ListView listView;

    //list数据
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    //省、市、县列表
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //被选中省、市、县
    private Province selectProvince;
    private City selectCity;

    //当前被选中级别
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area , container , false);
        title_text = (TextView)view.findViewById(R.id.title_text);
        back_button = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext() , android.R.layout.simple_list_item_1 , dataList);
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE){
                selectProvince = provinceList.get(position);
                queryCities();
            }else if (currentLevel == LEVEL_CITY){
                selectCity = cityList.get(position);
                queryCounty();
            }else if (currentLevel == LEVEL_COUNTY){
                String weatherId = countyList.get(position).getWeatherId();
                Intent intent = new Intent(getActivity() , WeatherActivity.class);
                intent.putExtra("weather_id" , weatherId);
                startActivity(intent);
                getActivity().finish();
            }
        });
        back_button.setOnClickListener(v -> {
            if (currentLevel == LEVEL_COUNTY){
                queryCities();
            }else if (currentLevel == LEVEL_CITY){
                queryProvince();
            }
        });
        queryProvince();
    }

    //查询省，先从数据库查询，后从服务器查询
    private void queryProvince(){
        title_text.setText("中国");
        back_button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromService(address , "province");
        }
    }

    //查询市，先从数据库查询，后从服务器查询
    private void queryCities(){
        title_text.setText(selectProvince.getProvinceName());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?" , String.valueOf(selectProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromService(address , "city");
        }
    }

    //查询县，先从数据库查询，后从服务器查询
    private void queryCounty(){
        title_text.setText(selectCity.getCityName());
        back_button.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?" , String.valueOf(selectCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/"+ cityCode;
            queryFromService(address , "county");
        }
    }

    //从服务器获取数据
    private void queryFromService(String address , final String type){
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    closeProgressDialog();
                    Toast.makeText(getContext() , "加载失败" , Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText , selectProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText , selectCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(() -> {
                        closeProgressDialog();
                        if ("province".equals(type)){
                            queryProvince();
                        }else if ("city".equals(type)){
                            queryCities();
                        }else if ("county".equals(type)){
                            queryCounty();
                        }
                    });
                }
            }
        });
    }

    //开启进度对话框
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
