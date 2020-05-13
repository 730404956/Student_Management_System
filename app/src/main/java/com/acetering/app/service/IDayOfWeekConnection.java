package com.acetering.app.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.acetering.app.IDayOfWeek;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/6
 */
public final class IDayOfWeekConnection implements ServiceConnection {
    private IDayOfWeek iDayOfWeek;

    public IDayOfWeek getiDayOfWeek() {
        return iDayOfWeek;
    }

    public IDayOfWeekConnection() {
    }

    public void onServiceConnected(ComponentName name, IBinder service) {

        iDayOfWeek = IDayOfWeek.Stub.asInterface(service);
    }

    public void onServiceDisconnected(ComponentName name) {
        iDayOfWeek = null;
    }
}