package com.acetering.app.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/7
 */
public class HttpUtil {
    private static HttpUtil instance;
    private OkHttpClient client = new OkHttpClient();
    private String mUrl;
    private Map<String, String> mParam;
    private HttpResponse mHttpResponse;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private HttpUtil(HttpResponse response) {
        this.mHttpResponse = response;
    }

    /**
     * get singleton instance and set response
     *
     * @param response call back when http response
     * @return
     */
    public static HttpUtil getHttpUtil(HttpResponse response) {
        if (instance == null) {
            instance = new HttpUtil(response);
        } else {
            instance.mHttpResponse = response;
        }
        return instance;
    }

    public interface HttpResponse {
        /**
         * call when http response
         *
         * @param responseString response string
         */
        void onSuccess(String responseString);

        /**
         * call when fail to get response due to network
         *
         * @param error failure reason in text
         */
        void onFail(String error);
    }

    public void sendPostHttp(String url, Map<String, String> param) {
        sendHttp(url, param, true);
    }

    public void sendGetHttp(String url, Map<String, String> param) {
        sendHttp(url, param, false);
    }

    private void sendHttp(String url, Map<String, String> param, boolean isPost) {
        this.mUrl = url;
        this.mParam = param;
        run(isPost);
    }

    private void run(boolean isPost) {
        //create request
        Request request = createRequest(isPost);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //check exist
                if (mHttpResponse != null) {
                    //post to main loop to execute
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHttpResponse.onFail("请求错误");
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (mHttpResponse != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!response.isSuccessful()) {
                                mHttpResponse.onFail("请求失败");
                            } else {
                                try {
                                    ResponseBody body = response.body();
                                    if (body != null) {
                                        String json = body.string();
                                        int index = json.indexOf("{");
                                        //make json string
                                        json = json.substring(index);
                                        Log.i("HTTP", "run: " + json);
                                        //deal with json response body
                                        mHttpResponse.onSuccess(json);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    mHttpResponse.onFail("结果转换失败");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private Request createRequest(boolean isPost) {
        Request request;
        //post method
        if (isPost) {
            //create builder
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
            //set type as form
            requestBodyBuilder.setType(MultipartBody.FORM);
            // create iterator for params
            Iterator<Map.Entry<String, String>> iterator = mParam.entrySet().iterator();
            //loop through params
            while ((iterator.hasNext())) {
                Map.Entry<String, String> entry = iterator.next();
                //add data to builder data part
                requestBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            //create request with url and data
            request = new Request.Builder().url(mUrl)
                    .post(requestBodyBuilder.build())
                    .build();
        } else {//get method
            //connect string to url
            String urlString = mUrl + "?" + MapParamToString(mParam);
            //create request
            request = new Request.Builder().url(urlString)
                    .build();
        }
        return request;
    }

    /**
     * transform map to string
     *
     * @param param map
     * @return string as key=value&...
     */
    private String MapParamToString(Map<String, String> param) {
        //use string builder to connect sub string
        StringBuilder stringBuilder = new StringBuilder();
        //loop through the map and connect each part
        Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            //pattern: key=value&key=value...
            stringBuilder.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        //remove the last '&' and create string
        String str = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        return str;
    }
}