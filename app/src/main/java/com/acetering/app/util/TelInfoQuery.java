package com.acetering.app.util;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/7
 */

import android.util.Log;


import java.util.HashMap;
import java.util.Map;

public class TelInfoQuery {
    private String TAG = "HTTP";
    //use tcc.taobao.com to get tel info
    private static final String mUrl = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
    private HttpUtil.HttpResponse response;

    public TelInfoQuery(HttpUtil.HttpResponse response) {
        this.response = response;
    }


    public void searchPhoneInfo(String phoneString) {
        if (phoneString.length() != 11) {
            Log.i(TAG, "searchPhoneInfo: tel number invalid");
            return;
        }
        //http request method
        sendHttp(phoneString);
    }

    private void sendHttp(String phoneString) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", phoneString);
        HttpUtil httpUtil = HttpUtil.getHttpUtil(response);
        httpUtil.sendGetHttp(mUrl, map);
    }
}