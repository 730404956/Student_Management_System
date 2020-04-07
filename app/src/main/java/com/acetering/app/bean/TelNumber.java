package com.acetering.app.bean;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/7
 */
public class TelNumber implements Serializable {
    private String telString;
    private String catName;
    private String province;
    private String carrier;

    public String getTelString() {
        return telString;
    }

    public void setTelString(String telString) {
        this.telString = telString;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    @NonNull
    @Override
    public String toString() {
        String text = "电话号码：" + telString + "\n运营商：" + catName + "\n归属地：" + province;
        return text;
    }
}
