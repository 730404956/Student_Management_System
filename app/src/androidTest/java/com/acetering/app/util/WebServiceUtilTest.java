package com.acetering.app.util;

import android.util.Log;

import com.acetering.app.bean.Weather;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.*;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/31
 */
public class WebServiceUtilTest {

    @Test
    public void getWether() {
        Map<String, String> map = new HashMap();
        map.put("theCityCode", "");
        map.put("theUserID", "");
        HttpUtil util = HttpUtil.getHttpUtil(new HttpUtil.HttpResponse() {
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
                    Weather[] weathers = new Weather[5];
                    for (int i = 0; i < 5; i++) {
                        String[] day = new String[5];
                        for (int j = 0; j < 5; j++) {
                            Node node = sList.item(7 + i * 5 + j);
                            String value = node.getChildNodes().item(0).getNodeValue();
                            day[j] = value;
                        }
                        weathers[i] = new Weather(day);
                    }
                    Log.i("", "onSuccess: " + weathers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
        util.sendGetHttp("http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather", map);
//        Log.i("", "getWether: ");
        int x = 0;
    }
}