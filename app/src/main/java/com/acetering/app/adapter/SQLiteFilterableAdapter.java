package com.acetering.app.adapter;

import android.content.Context;

import com.acetering.app.bean.Student;
import com.acetering.app.dao.StudentDAL;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/21
 */
public class SQLiteFilterableAdapter extends FiltableAdapter<Student> {
    private StudentDAL dal;
    private List<Student> newStudents;
    private List<Student> removedStudents;
    private List<Student> modifiedStudents;

    public SQLiteFilterableAdapter(Context context, List<Student> dataResources, int itemLayoutResourceId, ViewBinder binder) {
        super(context, dataResources, itemLayoutResourceId, binder);
        dal = new StudentDAL(context);
        if (dataResources == null) {
            dataResource = dal.getAllStudents();
            data = dataResource;
        }
    }

    @Override
    public void addItem(Student item) {
        super.addItem(item);
        if (newStudents == null) {
            newStudents = new ArrayList<>();
        }
        newStudents.add(item);
    }

    @Override
    public void removeItem(Student item) {
        super.removeItem(item);
        if (removedStudents == null) {
            removedStudents = new ArrayList<>();
        }
        removedStudents.add(item);
    }

    @Override
    public void modifyItem(int index, Student item) {
        super.modifyItem(index, item);
        if (modifiedStudents == null) {
            modifiedStudents = new ArrayList<>();
        }
        modifiedStudents.add(item);
    }

    @Override
    public void close() {
        super.close();
        if (removedStudents != null) {
            for (Student s : removedStudents) {
                dal.deleteStudent(s.getStu_id());
            }
        }
        if (modifiedStudents != null) {
            dal.updateAll(modifiedStudents);
        }
        if (newStudents != null) {
            dal.updateAll(newStudents);
        }
    }
}
