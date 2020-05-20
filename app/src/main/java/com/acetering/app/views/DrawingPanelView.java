package com.acetering.app.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.acetering.app.R;
import com.acetering.app.event.CallbackEvent;

import androidx.annotation.NonNull;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/18
 */
public class DrawingPanelView extends Dialog {
    int pen_color;
    ImageView color_view;
    Handler seek_bar_change_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                color_view.setImageDrawable(new ColorDrawable(pen_color));
            }
        }
    };

    public DrawingPanelView(@NonNull Context context, CallbackEvent onFinish) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.palette_view, null);
        setContentView(v);
        PaletteView paletteView = v.findViewById(R.id.palette);
        v.findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            onFinish.doJob(context, paletteView.getBitmap());
            cancel();
        });
        v.findViewById(R.id.btn_clear).setOnClickListener(v1 -> {
            paletteView.clear();
        });
        color_view = v.findViewById(R.id.color_img);
        ((SeekBar) v.findViewById(R.id.color_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
        ((SeekBar) v.findViewById(R.id.size_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        v.findViewById(R.id.btn_earser).setOnClickListener(v1 -> paletteView.setMode(PaletteView.Mode.ERASER));
        v.findViewById(R.id.btn_pen).setOnClickListener(v1 -> paletteView.setMode(PaletteView.Mode.DRAW));
        v.findViewById(R.id.btn_clear).setOnClickListener(v1 -> paletteView.clear());
        v.findViewById(R.id.btn_redo).setOnClickListener(v1 -> paletteView.redo());
        v.findViewById(R.id.btn_undo).setOnClickListener(v1 -> paletteView.undo());
    }
}
