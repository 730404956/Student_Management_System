package com.acetering.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.acetering.app.IDayOfWeek;

import java.util.Calendar;

public class QueryWeekdayService extends Service {
    private IBinder binder = new PersonQueryBinder();

    public QueryWeekdayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final class PersonQueryBinder extends IDayOfWeek.Stub {
        @Override
        public int getWeekday(int year, int month, int day) throws RemoteException {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            return c.get(Calendar.DAY_OF_WEEK);
        }
    }
}
