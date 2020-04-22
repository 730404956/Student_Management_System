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
public class SQLiteFiltableAdapter extends FiltableAdapter {
    StudentDAL dal;
    List<Student> newStudents;
    List<Student> removedStudents;
    List<Student> modifiedStudents;

    public SQLiteFiltableAdapter(Context context, List<Student> dataResources, int itemLayoutResourceId, ViewBinder binder) {
        super(context, dataResources, itemLayoutResourceId, binder);
        dal = new StudentDAL(context);
        if (dataResources == null) {
            dataResource = dal.getAllStudents();
            data = dataResource;
        }
    }

    @Override
    public void addItem(Object item) {
        super.addItem(item);
        if (newStudents == null) {
            newStudents = new ArrayList<>();
        }
        newStudents.add((Student) item);
    }

    @Override
    public void removeItem(Object item) {
        super.removeItem(item);
        if (removedStudents == null) {
            removedStudents = new ArrayList<>();
        }
        removedStudents.add((Student) item);
    }

    @Override
    public void modifyItem(int index, Object item) {
        super.modifyItem(index, item);
        if (modifiedStudents == null) {
            modifiedStudents = new ArrayList<>();
        }
        modifiedStudents.add((Student) item);
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
