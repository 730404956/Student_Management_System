package com.acetering.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.acetering.app.event.OnProgressReachedListener;

import androidx.annotation.Nullable;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/3/25
 */
public class TextCircleProgressBar extends View implements Runnable {
    private float max_progress = 10;
    private float current_progress = 0;
    private String TAG = "TextCirclePGBar";
    private Paint paint_main, paint_eraser, paint_text;
    private OnProgressReachedListener progressReachedListener;
    private float radius;
    private float inner_radius;
    private float textSize = 50;

    public TextCircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint_main = new Paint();
        paint_main.setColor(Color.BLUE);
        paint_eraser = new Paint();
        paint_eraser.setColor(Color.WHITE);
//        paint_eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        paint_text = new Paint();
        paint_text.setColor(Color.BLACK);
        paint_text.setTextSize(textSize);
        paint_text.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure: ");
    }

    @Override
    public void run() {
        while (postProgress(1)) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            postInvalidate();
        }
        if (progressReachedListener != null) {
            progressReachedListener.onProgressReached();
        }
    }

    public synchronized void setProgress(float progress) {
        if (progress >= 0) {
            this.current_progress = progress;
        }
    }

    public float getMax_progress() {
        return max_progress;
    }

    public synchronized boolean postProgress(float progress) {
        if (current_progress < max_progress) {
            this.current_progress += progress;
            return true;
        } else {
            return false;
        }
    }

    public void setProgressReachedListener(OnProgressReachedListener progressReachedListener) {
        this.progressReachedListener = progressReachedListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        radius = getWidth() / 2f;
        inner_radius = radius - 30f;
        canvas.drawCircle(radius, radius, radius, paint_main);

        canvas.drawCircle(radius, radius, inner_radius, paint_eraser);
        canvas.drawArc(0, 0, radius * 2, radius * 2, 0, current_progress / max_progress * 360, true, paint_eraser);
        canvas.drawText("" + (int) (max_progress - (int) current_progress), radius, radius, paint_text);
        Log.i(TAG, "onDraw: " + radius);
    }
}
