package com.acetering.app.share;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.acetering.app.bean.Student;
import com.acetering.app.dao.StudentDAL;
import com.acetering.app.dao.StudentDBHelper;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/6
 */
public class StudentContentProvider extends ContentProvider {

    //初始化一些常量
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private SQLiteDatabase db;
    private static StudentDAL studentDAL;
    //为了方便直接使用UriMatcher,这里addURI,下面再调用Matcher进行匹配

    static {
        matcher.addURI("com.acetering.app.providers.student", "test", 1);
    }

    @Override
    public boolean onCreate() {
        studentDAL = new StudentDAL(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (db == null) {
            db = new StudentDBHelper(getContext(), "student_db", null, 3).getReadableDatabase();
        }
        return db.rawQuery("select * from student_info;", null);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        switch (matcher.match(uri)) {
            //把数据库打开放到里面是想证明uri匹配完成
            case 1:
                Student stu = (Student) values.get("student");
                studentDAL.addStudent(stu);
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String id, String[] selectionArgs) {
        studentDAL.deleteStudent(id);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String id,
                      String[] selectionArgs) {
        Student stu = (Student) values.get("student");
        stu.setStu_id(id);
        studentDAL.changeStudentInfo(stu);
        return 0;
    }

}
