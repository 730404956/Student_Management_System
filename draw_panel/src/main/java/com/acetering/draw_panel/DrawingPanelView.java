package com.acetering.draw_panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/18
 */
public class DrawingPanelView extends AppCompatActivity {
    int pen_color;
    ImageView color_view;
    @SuppressLint("HandlerLeak")
    Handler seek_bar_change_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                color_view.setImageDrawable(new ColorDrawable(pen_color));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示
        setContentView(R.layout.palette_view);
        PaletteView paletteView = findViewById(R.id.palette);
        findViewById(R.id.btn_clear).setOnClickListener(v1 -> {
            paletteView.clear();
        });
        color_view = findViewById(R.id.color_img);
        ((SeekBar) findViewById(R.id.color_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String rgb = String.format("#%06X", progress);
                pen_color = Color.parseColor(rgb);
                seek_bar_change_handler.sendEmptyMessage(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                paletteView.setPenColor(pen_color);

            }
        });
        ((SeekBar) findViewById(R.id.size_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 1;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                paletteView.setSize(progress);
            }
        });
        findViewById(R.id.btn_earser).setOnClickListener(v1 -> paletteView.setMode(PaletteView.Mode.ERASER));
        findViewById(R.id.btn_pen).setOnClickListener(v1 -> paletteView.setMode(PaletteView.Mode.DRAW));
        findViewById(R.id.btn_clear).setOnClickListener(v1 -> paletteView.clear());
        findViewById(R.id.btn_redo).setOnClickListener(v1 -> paletteView.redo());
        findViewById(R.id.btn_undo).setOnClickListener(v1 -> paletteView.undo());
    }
}

