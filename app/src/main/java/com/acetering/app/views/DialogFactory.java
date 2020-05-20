package com.acetering.app.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.acetering.app.IDayOfWeek;
import com.acetering.app.R;
import com.acetering.app.event.CallbackEvent;
import com.acetering.app.event.OnProgressReachedListener;
import com.acetering.app.util.SoundUtil;

import androidx.appcompat.app.AlertDialog;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/7
 */
public class DialogFactory {
    /**
     * create progress dialog that can't be cancel by click outside dialog
     *
     * @param context activity context
     * @param title   dialog title
     * @return dialog
     */
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

    /**
     * create ads dialog that show a picture and will be canceled after seconds
     *
     * @param onProgressReachedListener call when reached max progress
     * @param bg                        background picture
     * @param lastSeconds               dialog will be automatically canceled after the time(seconds)
     */
    public static AlertDialog createAdsDialog(Context context, OnProgressReachedListener onProgressReachedListener, Drawable bg, float lastSeconds) {
        AlertDialog adsDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.ads_view, null);
        ImageView img = v.findViewById(R.id.imageView);
        if (bg != null) {
            img.setImageDrawable(bg);
        }
        builder.setView(v);
        TextCircleProgressBar circleProgressBar = v.findViewById(R.id.pgBar);
        circleProgressBar.setMax_progress(lastSeconds);
        //change to main activity after progress reached
        circleProgressBar.setProgressReachedListener(onProgressReachedListener);
        //set click to skip ads
        circleProgressBar.setOnClickListener(v1 -> circleProgressBar.setProgress(circleProgressBar.getMax_progress()));
        //create ads dialog
        adsDialog = builder.create();
        //start progress when shown in view
        adsDialog.setOnShowListener(dialog -> {
            Thread thread = new Thread(circleProgressBar);
            try {
                thread.start();
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
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

        btn_confirm.setOnClickListener(v1 -> {
            int day_of_week = -1;
            try {
                day_of_week = iDayOfWeek.getWeekday(Integer.parseInt(year.getText().toString()), Integer.parseInt(month.getText().toString()) - 1, Integer.parseInt(day.getText().toString()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "星期" + new String[]{"日", "一", "二", "三", "四", "五", "六"}[day_of_week - 1], Toast.LENGTH_SHORT).show();
        });
        dialog = builder.create();
        return dialog;
    }

    public static AlertDialog createChooseDialog(Context context, String title, String[] items, DialogInterface.OnClickListener listener) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(items, listener);
        dialog = builder.create();
        return dialog;
    }

    public static AlertDialog createPaletteDialog(Context context, CallbackEvent onFinish) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.palette_view, null);
        builder.setView(v);
        builder.setCancelable(false);
        dialog = builder.create();
        Button btn_confirm = v.findViewById(R.id.btn_confirm);
        PaletteView paletteView = v.findViewById(R.id.palette);
        btn_confirm.setOnClickListener(view -> {
            onFinish.doJob(context, paletteView.getBitmap());
            dialog.cancel();
        });
        v.findViewById(R.id.btn_cancel).setOnClickListener(v1 -> {
            paletteView.clear();
        });
        dialog.setOnShowListener(dialog1 -> {
            paletteView.clear();
        });
        return dialog;
    }

    public static AlertDialog createSoundRecorderDialog(Context context, String filepath) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.sound_recorder, null);
        v.findViewById(R.id.btn_record).setOnTouchListener((v12, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    SoundUtil.startRecord(context, filepath);
                    break;
                case MotionEvent.ACTION_UP:
                    SoundUtil.stopRecord();
                    break;
            }
            v12.performClick();
            return false;
        });
        v.findViewById(R.id.btn_play).setOnClickListener(v1 -> SoundUtil.play(context, filepath));
        builder.setView(v);
        dialog = builder.create();
        return dialog;
    }
}
