package com.acetering.app.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.acetering.app.IDayOfWeek;
import com.acetering.app.R;
import com.acetering.app.event.OnProgressReachedListener;

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
            @Override
            public void onShow(DialogInterface dialog) {
                Thread thread = new Thread(circleProgressBar);
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

    public static AlertDialog createQueryWeekdayDialog(Context context, IDayOfWeek iDayOfWeek) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.query_weekday, null);
        builder.setView(v);
        Button btn_confirm = v.findViewById(R.id.btn_confirm);
        final EditText year = v.findViewById(R.id.year);
        final EditText month = v.findViewById(R.id.month);
        final EditText day = v.findViewById(R.id.day);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day_of_week = -1;
                try {

                    day_of_week = iDayOfWeek.getWeekday(Integer.parseInt(year.getText().toString()), Integer.parseInt(month.getText().toString()) - 1, Integer.parseInt(day.getText().toString()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, "星期" + new String[]{"日", "一", "二", "三", "四", "五", "六"}[day_of_week - 1], Toast.LENGTH_SHORT).show();
            }
        });
        dialog = builder.create();
        return dialog;
    }
}
