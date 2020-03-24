package com.acetering.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.acetering.app.adapter.MyFragmentAdapter;
import com.acetering.app.bean.Student;
import com.acetering.student_input.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewManagerActivity extends AppCompatActivity {

    private String[] stu_names = new String[]{"Jobs", "Lena"};
    private String[] stu_ids = new String[]{"25037593", "33268512"};
    private String[] genders = new String[]{"男", "女"};
    private Date[] birthdays = new Date[]{new Date(), new Date()};
    private String[] colleagues = new String[]{"计算机学院", "电气学院"};
    private String[] majors = new String[]{"软件工程", "电气工程"};
    ViewPager pager;
    MyFragmentAdapter adapter;

    private static ViewManagerActivity instance;
    FragmentManager fManager;
    FragmentMain fragment_main;
    FragmentStudent fragment_student;
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
        fragment_main = new FragmentMain();
        fragment_student = new FragmentStudent();
        instance = this;
        loadData();
        pager = findViewById(R.id.ly_content);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(fragment_main);
        fragments.add(fragment_student);
        adapter = new MyFragmentAdapter(fManager, fragments);
        pager.setAdapter(adapter);
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

    private void changeFragment(Fragment fragment) {
        int index = adapter.getItemId(fragment);
        Log.i("view manager", "changeFragment: " + index);
        pager.setCurrentItem(index);
    }


    public void changeToStudentFragment(Student student) {
        if (fragment_student == null) {
            fragment_student = new FragmentStudent();
            fragment_student.setStudent(student);
        } else {
            fragment_student.setStudent(student);
            changeFragment(fragment_student);
        }
    }

    public void changeToStudentFragment() {
        changeToStudentFragment(null);
    }

    public void changeToMainFragment() {
        if (fragment_main == null) {
            fragment_main = new FragmentMain();
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

}
