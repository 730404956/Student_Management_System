package com.acetering.app.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acetering.student_input.R;

public class ImageToast extends Toast {
    protected static ImageToast toast;

    private ImageToast(Context context) {
        super(context);
    }

    public static ImageToast make(Context context, int imageId, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_image_toast, null);
        ((ImageView) view.findViewById(R.id.img)).setImageDrawable(context.getDrawable(imageId));
        ((TextView) view.findViewById(R.id.text)).setText(message);
        if (toast == null) {
            toast = new ImageToast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setView(view);
        return toast;
    }
}
