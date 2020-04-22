package com.acetering.app.dao;

import com.acetering.app.bean.Student;

import java.util.List;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/21
 */
public interface StudentDAO {
    boolean addStudent(Student student);

    boolean deleteStudent(String student_id);

    boolean changeStudentInfo(Student student);

    boolean saveStudent(Student student);

    Student getStudentById(String student_id);

    List<Student> getAllStudents();

    boolean[] updateAll(List<Student> students);
}
