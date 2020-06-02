package com.acetering.app.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/31
 */
public class Weather {
    private String date;
    private String desc;
    private String high_temp;
    private String low_temp;
    private String wind;
    private String icon1;
    private String icon2;

    public Weather(String[] weather_data) {
        date = weather_data[0].split(" ")[0];
        desc = weather_data[0].split(" ")[1];
        String[] temp = weather_data[1].replace("℃", "").split("/");
        high_temp = temp[0];
        low_temp = temp[1];
        wind = weather_data[2];
        icon1 = weather_data[3];
        icon2 = weather_data[4];
    }

    public String getDate() {
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getHigh_temp() {
        return high_temp;
    }

    public String getLow_temp() {
        return low_temp;
    }

    public String getWind() {
        return wind;
    }

    public String getIcon1() {
        return icon1;
    }

    public String getIcon2() {
        return icon2;
    }
}
