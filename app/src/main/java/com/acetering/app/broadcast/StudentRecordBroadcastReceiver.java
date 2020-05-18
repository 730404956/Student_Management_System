package com.acetering.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.acetering.app.ViewManagerActivity;
import com.acetering.app.bean.Student;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/18
 */
public class StudentRecordBroadcastReceiver extends BroadcastReceiver {
    String TAG = "CPBD";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        String id = intent.getStringExtra("stu_id");
        Toast.makeText(context, "recieved:" + id, Toast.LENGTH_SHORT).show();
        ViewManagerActivity.getInstance().changeToStudentFragment(new Student(id));
    }
}
