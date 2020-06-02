package com.acetering.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.acetering.app.adapter.MyFragmentAdapter;
import com.acetering.app.bean.Student;
import com.acetering.app.service.ClipboardMonitorService;
import com.acetering.app.service.IDayOfWeekConnection;
import com.acetering.app.service.NetworkObserver;
import com.acetering.app.service.QueryWeekdayService;
import com.acetering.app.util.AppConfig;
import com.acetering.app.views.DialogFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class ViewManagerActivity extends AppCompatActivity {

    private String TAG = "View Manager";
    ViewPager pager;
    MyFragmentAdapter adapter;

    private static ViewManagerActivity instance;
    FragmentManager fManager;
    FragmentMain fragment_main;
    ActivityConfig fragment_setting;
    FragmentStudent fragment_student;
    private AlertDialog searchDialog;

    private Intent net_obeserver, clipboard_monitor;
    private IDayOfWeekConnection conn;
    private AlertDialog query_weekday_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_manager);
        AppConfig.applyConfig(this);
        //get fragment manager
        fManager = getSupportFragmentManager();
        fragment_main = new FragmentMain();
        fragment_student = new FragmentStudent();
        fragment_setting = new ActivityConfig();
        instance = this;
        pager = findViewById(R.id.ly_content);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(fragment_student);
        fragments.add(fragment_main);
        fragments.add(fragment_setting);
        adapter = new MyFragmentAdapter(fManager, fragments);
        pager.setAdapter(adapter);
        changeToMainFragment();
        net_obeserver = new Intent(this, NetworkObserver.class);
        //start service to monitor network
        startService(net_obeserver);
        //start service to monitor clipboard
        clipboard_monitor = new Intent(this, ClipboardMonitorService.class);
        startService(clipboard_monitor);

        Log.i(TAG, "---onCreate---");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Log.i(TAG, "---onCreateOptionsMenu---");
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
            case R.id.query_tel_info_item:
                startActivity(new Intent("com.acetering.student_input.QUERY_TEL_INFO"));
                break;
            case R.id.query_weekday_item:
                if (query_weekday_dialog == null) {
                    Intent intent = new Intent(this, QueryWeekdayService.class);
                    conn = new IDayOfWeekConnection();
                    bindService(intent, conn, BIND_AUTO_CREATE);
                    query_weekday_dialog = DialogFactory.createQueryWeekdayDialog(this, conn.getiDayOfWeek());
                }
                query_weekday_dialog.show();
                break;
            case R.id.query_weather:
                startActivity(new Intent("com.acetering.student_input.QUERY_WEATHER"));
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
            builder.setPositiveButton(R.string.search, (dialog, which) -> {
                String input_text = input.getText().toString();
                fragment_main.filter(input_text);
                searchDialog.cancel();
            });
            builder.setNegativeButton(R.string.clear_filter, (dialog, which) -> {
                fragment_main.clearFilter();
                searchDialog.cancel();
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

    public void replaceMainFragment(FragmentMain fragment_main) {
        this.fragment_main = fragment_main;
        adapter.replace(fragment_main, 1);
    }

    public void replaceStudentFragment(FragmentStudent fragment) {
        this.fragment_student = fragment;
        adapter.replace(fragment_student, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "---onStart:---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "---onStop:---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(net_obeserver);
        if (conn != null) {
            unbindService(conn);
        }
        stopService(clipboard_monitor);
        Log.i(TAG, "---onDestroy---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "---onPause:---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "---onResume---");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void addNewStudent(Student student) {
        fragment_main.addNewStudent(student);
    }

    public static ViewManagerActivity getInstance() {
        return instance;
    }

}
