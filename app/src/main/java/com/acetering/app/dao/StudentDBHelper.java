package com.acetering.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/21
 */
public class StudentDBHelper extends SQLiteOpenHelper {
    String TAG = "DBHelper";

    public StudentDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static String getCreateTableSQL() {
        return "create table student_info(id varchar(20) primary key,name varchar(40),gender varchar(10),colleague text,major text,birthday date,description text)";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableSQL());
        db.execSQL("insert into student_info values('3170208999','Jobs','男','计算机学院','计算机科学','2020-04-22','');");
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: " + oldVersion + "-->" + newVersion);
        switch (newVersion) {
            case 2:
                db.execSQL("alter table student_info add column description text");
        }
    }
}
