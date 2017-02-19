package com.vince.my_weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vince.my_weather.db.City;
import com.vince.my_weather.db.County;
import com.vince.my_weather.db.Province;
import com.vince.my_weather.util.HttpUtil;
import com.vince.my_weather.util.Utility;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/18.
 */

public class ChooseAreaFragment extends Fragment {//把省列表数据所有逻辑写在这里
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentLevel;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectProvince;
    private City selectCity;

    private ArrayList<String> datalist = new ArrayList<>();//显示listview的链表数据
    private ArrayAdapter<String> adapter;
    private ListView listView;

    private Button backbutton;
    private TextView title;


    @Override//创建视图时执行的方法，这里进行控件的初始化
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chose_area, container, false);
        backbutton = (Button) view.findViewById(R.id.backbutton);//注意这里要通过view才能用findViewById.最外面那层布局
        title = (TextView) view.findViewById(R.id.titlename);
        listView = (ListView) view.findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);
        return view;
    }

    @Override//和碎片关联的actvity创建完毕时执行,这里进行主要的逻辑
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvince();
    }

    public void queryProvince() {
        title.setText("中国");
        provinceList = DataSupport.findAll(Province.class);//遍历Province表，存到链表中去
        if (provinceList.size() > 0) {
            datalist.clear();//先把列表的数据清空！！！
            for (Province p : provinceList) {
                datalist.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFormServer(address, "province");
        }
    }

    public void queryFormServer(String address, final String type) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w(getContext().toString(), "错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Boolean flag = false;
                String data = response.body().string();//获取服务器返回的数据
                if (type.equals("province")) {
                    flag = Utility.handleProvinceResponse(data);//解析数据把省数据存到本地数据库
                }
                if (flag) {
                    getActivity().runOnUiThread(new Runnable() {//回到主线程修改UI
                        @Override
                        public void run() {
                            if (type.equals("province")) {
                                queryProvince();
                            }
                        }
                    });
                }
            }
        });

    }

}
