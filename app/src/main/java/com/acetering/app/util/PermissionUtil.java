package com.acetering.app.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/22
 */
public class PermissionUtil {
    private static PermissionUtil instance;
    private Context context;

    private PermissionUtil() {

    }

    public static PermissionUtil getInstance(Context context) {
        if (instance == null) {
            instance = new PermissionUtil();
        }
        instance.context = context;
        return instance;
    }

    public boolean checkReadPermission() {
        // 检查权限是否获取（android6.0及以上系统可能默认关闭权限，且没提示）
        PackageManager pm = context.getPackageManager();
        boolean permission_readStorage = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", context.getPackageName()));
        return permission_readStorage;
    }

    public void getReadPermission() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 0x01);
    }

}
