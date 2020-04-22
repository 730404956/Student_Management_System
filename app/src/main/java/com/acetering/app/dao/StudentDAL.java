package com.acetering.app.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.acetering.app.bean.Student;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/21
 */
public class StudentDAL implements StudentDAO {
    SQLiteDatabase db;
    String TAG = "DB";
    public final static int db_version = 2;

    public StudentDAL(Context context) {
        StudentDBHelper helper = new StudentDBHelper(context, "student_db", null, db_version);
        db = helper.getReadableDatabase();
    }

    @Override
    public boolean addStudent(Student student) {
        try {
            db.execSQL("insert into student_info values(?,?,?,?,?,?,?)", new String[]{student.getStu_id(), student.getStu_name(), student.getGender(), student.getColleague(), student.getMajor(), new SimpleDateFormat("yyyy-MM-dd").format(student.getBirthday()), student.getDescription()});

            Log.i(TAG, "addStudent: " + student.getStu_name());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteStudent(String student_id) {
        try {
            db.execSQL("delete from student_info where id=?", new String[]{student_id});
            Log.i(TAG, "deleteStudent id:" + student_id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean changeStudentInfo(Student student) {
        try {
            db.execSQL("update student_info set name=? ,gender=?,colleague=?,major=?,birthday=?,description=?  where id=?;", new String[]{student.getStu_name(), student.getGender(), student.getColleague(), student.getMajor(), new SimpleDateFormat("yyyy-MM-dd").format(student.getBirthday()), student.getDescription(), student.getStu_id()});

            Log.i(TAG, "changeStudentInfo: " + student.getStu_name());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean saveStudent(Student student) {
        if (!addStudent(student)) {
            return changeStudentInfo(student);
        }
        return true;
    }

    @Override
    public Student getStudentById(String student_id) {
        return null;
    }

    @Override
    public List<Student> getAllStudents() {
        Log.i(TAG, "getAllStudents");
        Cursor cursor = db.rawQuery("select * from student_info;", null);
        List<Student> students = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return students;
        }
        do {
            String stu_id = cursor.getString(0);
            String stu_name = cursor.getString(1);
            String stu_gender = cursor.getString(2);
            String stu_colleague = cursor.getString(3);
            String stu_major = cursor.getString(4);
            Date birthday = Date.valueOf(cursor.getString(5));
            String stu_description = cursor.getString(6);
            Student s = new Student(stu_name, stu_id, stu_gender, birthday, stu_colleague, stu_major, stu_description);
            students.add(s);
        } while (cursor.moveToNext());
        cursor.close();
        return students;
    }

    @Override
    public boolean[] updateAll(List<Student> students) {
        boolean[] success = new boolean[students.size()];
        int i = 0;
        for (Student s : students) {
            success[i++] = saveStudent(s);
        }
        return success;
    }
}
