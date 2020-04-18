package com.acetering.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.acetering.app.dao.FakeUserDAO;


/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/18
 */
public class AppConfig {
    private static String TAG = "AppConfig";

    public static void applyConfig(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enable_landscape = sp.getBoolean("enable_landscape", false);
        float font_size = Float.parseFloat(sp.getString("text_size", "1f"));
        Resources res = context.getResources();
//        set font size
        Configuration cf = res.getConfiguration();
        boolean need_recreate = Math.abs(cf.fontScale - font_size) > 0.01;
//        Log.i(TAG, "applyConfig: "+cf.fontScale+"    ****   "+font_size);
        cf.fontScale = font_size;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            //set screen orientation
            if (enable_landscape) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            }
            if (need_recreate) {
                activity.recreate();
            }
        }
        res.updateConfiguration(cf, res.getDisplayMetrics());
    }

    public static void changeAccountPreference(Context context) {
        //set id and password
        FakeUserDAO.getInstance(context).update();
    }
}
