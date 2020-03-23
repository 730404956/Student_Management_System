package com.acetering.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import com.acetering.app.bean.Student;
import com.acetering.student_input.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewManagerActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {


    private String[] stu_names = new String[]{"Jobs", "Lena"};
    private String[] stu_ids = new String[]{"25037593", "33268512"};
    private String[] genders = new String[]{"男", "女"};
    private Date[] birthdays = new Date[]{new Date(), new Date()};
    private String[] colleagues = new String[]{"计算机学院", "电气学院"};
    private String[] majors = new String[]{"软件工程", "电气工程"};


    private static ViewManagerActivity instance;
    FragmentManager fManager;
    ActivityMain fragment_main;
    ActivityStudent fragment_student;
    TextView t1, t2;
    List<Student> datas;
    private AlertDialog searchDialog;
    private int counter = 0;


    private void loadData() {
        datas = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            datas.add(new Student(stu_names[i], stu_ids[i], genders[i], birthdays[i], colleagues[i], majors[i]));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_manager);
        //get fragment manager
        fManager = getSupportFragmentManager();
        t1 = findViewById(R.id.t1);
        t1.setOnClickListener(this);
        t2 = findViewById(R.id.t2);
        t2.setOnClickListener(this);
        instance = this;
        loadData();
        Log.i("ViewManagerActivity", "onCreate: " + counter++);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_stu_menu_item:
                fragment_main.recordNewStudent();
                break;
            case R.id.search_menu_item:
                showSearchDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearchDialog() {
        if (searchDialog == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getText(R.string.search));
            View view = LayoutInflater.from(this).inflate(R.layout.input_dialog, null);
            builder.setView(view);
            EditText input = view.findViewById(R.id.editText);
            builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input_text = input.getText().toString();
                    fragment_main.filter(input_text);
                    searchDialog.cancel();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fragment_main.clearFilter();
                    searchDialog.cancel();
                }
            });
            searchDialog = builder.create();
        }
        searchDialog.show();
    }

    @Override
    public void onClick(View v) {
        Log.i("view manager", "onClick: " + v.isSelected());
        if (!v.isSelected()) {
            if (v == t1) {
                t2.setSelected(false);
                t1.setSelected(true);
                changeToMainFragment();
            } else if (v == t2) {
                t2.setSelected(true);
                t1.setSelected(false);
                changeToStudentFragment();
            }
        }
    }

    private void changeFragment(Fragment fragment) {
        assert (fragment != null);
        FragmentTransaction transaction = fManager.beginTransaction();
        if (fragment_main != null)
            transaction.hide(fragment_main);
        if (fragment_student != null)
            transaction.hide(fragment_student);
        transaction.show(fragment);
        transaction.commit();

    }


    public void changeToStudentFragment(Student student) {
        t1.setSelected(false);
        t2.setSelected(true);
        if (fragment_student == null) {
            fragment_student = new ActivityStudent(this);
            fragment_student.setStudent(student);
            fManager.beginTransaction().add(R.id.ly_content, fragment_student).commit();
        } else {
            fragment_student.setStudent(student);
            changeFragment(fragment_student);
        }
    }

    public void changeToStudentFragment() {
        changeToStudentFragment(null);
    }

    public void changeToMainFragment() {
        t1.setSelected(true);
        t2.setSelected(false);
        if (fragment_main == null) {
            fragment_main = new ActivityMain(this);
            fManager.beginTransaction().add(R.id.ly_content, fragment_main).commit();
            return;
        }
        changeFragment(fragment_main);
    }

    public void addNewStudent(Student student) {
        fragment_main.addNewStudent(student);
    }

    public static ViewManagerActivity getInstance() {
        return instance;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
