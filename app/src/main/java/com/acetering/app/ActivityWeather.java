package com.acetering.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.acetering.app.adapter.BasicAdapter;
import com.acetering.app.bean.Weather;
import com.acetering.app.util.HttpUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ActivityWeather extends AppCompatActivity {
    public final static int UPDATE_TITLE = 0x00;
    BasicAdapter<Weather> weatherBasicAdapter;
    static List<Weather> weathers = new ArrayList<>();
    WeatherHandler handler = new WeatherHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherBasicAdapter = new BasicAdapter<Weather>(this, weathers, R.layout.weather_item, (holder, item) -> {
            Resources resources = getResources();
            AssetManager assetManager = resources.getAssets();
            Bitmap icon_1, icon_2;
            try {
                icon_1 = BitmapFactory.decodeStream(assetManager.open("weather_icon_hd/a_" + item.getIcon1()));
                if (item.getIcon1().equals(item.getIcon2())) {
                    holder.setVisibility(R.id.icon2, View.GONE);
                } else {
                    icon_2 = BitmapFactory.decodeStream(assetManager.open("weather_icon_hd/a_" + item.getIcon2()));
                    holder.setImage(R.id.icon2, new BitmapDrawable(resources, icon_2));
                }
            } catch (IOException e) {
                e.printStackTrace();
                icon_1 = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_4444);
            }
            holder.setImage(R.id.icon1, new BitmapDrawable(resources, icon_1))
                    .setText(R.id.high_temp, item.getHigh_temp())
                    .setText(R.id.low_temp, item.getLow_temp())
                    .setText(R.id.date, item.getDate())
                    .setText(R.id.desc, item.getDesc());
        });
        ((ListView) findViewById(R.id.weathers)).setAdapter(weatherBasicAdapter);
        updateWeather();
    }

    private void updateWeather() {
        Map<String, String> map = new HashMap();
        map.put("theCityCode", "镇江");
        map.put("theUserID", "");
        HttpUtil.getHttpUtil(new WeatherResponse(weatherBasicAdapter, handler)).sendGetHttp("http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather", map);
    }

    static class WeatherResponse implements HttpUtil.HttpResponse {
        BasicAdapter<Weather> adapter;
        WeatherHandler handler;

        private WeatherResponse(BasicAdapter<Weather> adapter, WeatherHandler handler) {
            this.adapter = adapter;
            this.handler = handler;

        }

        @Override
        public void onSuccess(String responseString) {
            Log.i("", "onSuccess: " + responseString);
            //1.创建DocumentBuilderFactory对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //2.创建DocumentBuilder对象
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document d = builder.parse(new ByteArrayInputStream(responseString.getBytes()));
                NodeList sList = d.getElementsByTagName("string");
                String city = sList.item(1).getChildNodes().item(0).getNodeValue();
                Message message = new Message();
                message.what = UPDATE_TITLE;
                Bundle bundle = new Bundle();
                bundle.putString("city", city);
                message.setData(bundle);
                weathers.clear();
                adapter.notifyDataSetChanged();
                handler.sendMessage(message);
                for (int i = 0; i < 5; i++) {
                    String[] day = new String[5];
                    for (int j = 0; j < 5; j++) {
                        Node node = sList.item(7 + i * 5 + j);
                        String value = node.getChildNodes().item(0).getNodeValue();
                        day[j] = value;
                    }
                    adapter.addItem(new Weather(day));
                }
                Log.i("", "onSuccess: " + weathers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFail(String error) {

        }
    }

    class WeatherHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TITLE:
                    ((TextView) findViewById(R.id.title)).setText(msg.getData().getString("city"));
            }
        }
    }
}
