package com.acetering.app.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.acetering.app.ActivityLogin;
import com.acetering.app.ViewManagerActivity;
import com.acetering.app.event.OnProgressReachedListener;
import com.acetering.student_input.R;

import androidx.appcompat.app.AlertDialog;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/7
 */
public class DialogFactory {

    public static AlertDialog createProgressDialog(Context context, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view_custom = LayoutInflater.from(context).inflate(R.layout.progressbar_dialog, null, false);
        //设置标题
        builder.setTitle(title);
        //设置布局
        builder.setView(view_custom);
        //设置不可取消
        builder.setCancelable(false);
        //create dialog
        return builder.create();
    }

    public static AlertDialog createAdsDialog(Context context, OnProgressReachedListener onProgressReachedListener, Drawable bg) {
        AlertDialog adsDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.ads_view, null);
        ImageView img = v.findViewById(R.id.imageView);
        if (bg != null) {
            img.setImageDrawable(bg);
        }
        builder.setView(v);
        TextCircleProgressBar circleProgressBar = v.findViewById(R.id.pgBar);
        //change to main activity after progress reached
        circleProgressBar.setProgressReachedListener(onProgressReachedListener);
        //set click to skip ads
        circleProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleProgressBar.setProgress(circleProgressBar.getMax_progress());
            }
        });
        //create ads dialog
        adsDialog = builder.create();
        //start progress when shown in view
        adsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            Thread thread = new Thread(circleProgressBar);

            @Override
            public void onShow(DialogInterface dialog) {
                try {
                    thread.start();
                } catch (IllegalThreadStateException e) {
                    e.printStackTrace();
                }
            }
        });
        adsDialog.setCancelable(false);
        return adsDialog;
    }
}
