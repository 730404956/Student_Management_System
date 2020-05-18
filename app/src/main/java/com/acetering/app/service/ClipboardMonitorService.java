package com.acetering.app.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ClipboardMonitorService extends Service {
    String TAG = "CPBD";
    ClipboardManager cm;
    Context context;
    static String TRIGGER_STR = "SE123456";
    String clipboard_content;

    public ClipboardMonitorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        check();
        cm.addPrimaryClipChangedListener(this::check);
    }


    private void check() {
        String new_content = getClipboardContent();
        if (clipboard_content != null && clipboard_content.equals(new_content)) {
            return;
        }
        clipboard_content = getClipboardContent();
        Log.i(TAG, "get board info:" + clipboard_content);
        if (clipboard_content != null && clipboard_content.equals(TRIGGER_STR)) {
            Log.i(TAG, "send broadcast");
            Intent it = new Intent("com.acetering.app.CLIPBOARD_MONITOR_ADD_STUDENT");
            //important, customize intent action has to set receiver's package name
            it.setPackage("com.acetering.app");
            it.putExtra("stu_id", clipboard_content);
            sendBroadcast(it);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 获取剪切板上的内容
     */
    @Nullable
    public String getClipboardContent() {
        if (cm != null) {
            ClipData data = cm.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                ClipData.Item item = data.getItemAt(0);
                if (item != null) {
                    CharSequence sequence = item.coerceToText(context);
                    if (sequence != null) {
                        return sequence.toString();
                    }
                }
            }
        } else {
            cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        return null;
    }
}
