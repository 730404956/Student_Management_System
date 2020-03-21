package com.acetering.app.bean;

import com.acetering.app.adapter.IFiltableData;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Student implements Serializable, IFiltableData {
    private String stu_name;
    private String stu_id;
    private String gender;
    private String colleague;
    private String major;
    private Date birthday;

    public Student(String stu_name, String stu_id, String gender, Date birthday, String colleague, String major) {
        this.stu_name = stu_name;
        this.stu_id = stu_id;
        this.gender = gender;
        this.birthday = birthday;
        this.colleague = colleague;
        this.major = major;

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getColleague() {
        return colleague;
    }

    public void setColleague(String colleague) {
        this.colleague = colleague;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Student) {
            return this.stu_id.equals(((Student) obj).stu_id);
        } else {
            return false;
        }
    }

    public static Student copy(Student a, Student student) {
        if (a == null) {
            return student;
        }
        a.stu_name = student.stu_name;
        a.stu_id = student.stu_id;
        a.gender = student.gender;
        a.colleague = student.colleague;
        a.major = student.major;
        a.birthday = (Date) student.birthday.clone();
        return a;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("姓名：%s\n性别：%s\n生日：%s\n学号：%s\n学院：%s\n班级：%s\n您要做什么？",
                this.getStu_name(), this.getGender(), sf.format(this.getBirthday()), this.getStu_id(), this.getColleague(), this.getMajor());
    }

    @Override
    public Map<String, String> getFiltData() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> result = new HashMap<>();
        result.put("name", stu_name);
        result.put("id", stu_id);
        result.put("birthday", sf.format(this.getBirthday()));
        result.put("gender", gender);
        result.put("colleague", colleague);
        result.put("major", major);
        return result;
    }
}
