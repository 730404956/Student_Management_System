package com.acetering.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acetering.app.views.ImageToast;
import com.acetering.app.adapter.FiltableAdapter;
import com.acetering.app.bean.Student;
import com.acetering.app.event.CallbackEvent;
import com.acetering.student_input.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityMain extends AppCompatActivity {
    public static ActivityMain instance;
    private ListView stu_list;
    private TextView search_filter;
    private List<Student> datas;
    private FiltableAdapter<Student> adapter;
    private String[] stu_names = new String[]{"Jobs", "Lena"};
    private String[] stu_ids = new String[]{"25037593", "33268512"};
    private String[] genders = new String[]{"男", "女"};
    private Date[] birthdays = new Date[]{new Date(), new Date()};
    private String[] colleagues = new String[]{"计算机学院", "电气学院"};
    private String[] majors = new String[]{"软件工程", "电气工程"};
    private AlertDialog.Builder builder;
    private AlertDialog itemMenuDialog, itemDeleteConfirmDialog;
    private AlertDialog exitDialog;
    private AlertDialog searchDialog;
    private Student current_student;
    Intent it;
    Bundle bd;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_stu_menu_item:
                recordNewStudent();
                break;
            case R.id.search_menu_item:
                if (searchDialog == null) {
                    searchDialog = createSearchDialog();
                }
                searchDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (instance == null) {
            instance = this;
        } else {
            finish();
        }
        builder = new AlertDialog.Builder(ActivityMain.this);
        exitDialog = createExitDialog();
        itemMenuDialog = createItemMenuDialog();
        itemDeleteConfirmDialog = createConfirmDeleteDialog();
        it = new Intent(ActivityMain.this, ActivityStudent.class);
        loadData();
        bindView();
    }

    private void loadData() {
        datas = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            datas.add(new Student(stu_names[i], stu_ids[i], genders[i], birthdays[i], colleagues[i], majors[i]));
        }
    }

    /***
     * 绑定视图
     */
    private void bindView() {
        search_filter = findViewById(R.id.search_text);
        stu_list = findViewById(R.id.stu_list);
        ImageView btn_record = findViewById(R.id.btn_record_student);
        //新增学生
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordNewStudent();
            }
        });
        //显示适配器视图绑定配置
        adapter = new FiltableAdapter<Student>(this, datas, R.layout.student_info_item) {
            @Override
            protected void bindView(ViewHolder holder, Student item) {
                String sex_male = getString(R.string.sex_male);
                holder.setText(R.id.stu_name, item.getStu_name())
                        .setText(R.id.stu_id, item.getStu_id())
                        .setText(R.id.stu_colleague, item.getColleague())
                        .setText(R.id.stu_major, item.getMajor())
                        .setImage(R.id.gender_img, getDrawable(item.getGender().equals(sex_male) ? R.drawable.male : R.drawable.female))
                        .setImage(R.id.stu_img, getDrawable(item.getGender().equals(sex_male) ? R.drawable.jobs : R.drawable.lena));
            }
        };
        adapter.setOnDataSetInvalid(new CallbackEvent() {
            @Override
            public void doJob(Context context) {
                ImageToast.make(ActivityMain.this, R.drawable.no, "没有查询到任何学生的信息！").show();
            }
        });
        adapter.setOnResult(new CallbackEvent() {
            @Override
            public void doJob(Context context) {
                search_filter.setText(search_filter.getTag().toString() + " 共有" + adapter.getCount() + "条结果。");
            }
        });
        //设置列表项监听
        stu_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //获取选中的学生对象
                current_student = adapter.getItem(position);
                //更新对话框显示文本
                itemMenuDialog.setMessage(current_student.toString());
                //显示菜单
                itemMenuDialog.show();
                return false;
            }
        });
        stu_list.setAdapter(adapter);
    }

    /***
     * 创建列表项菜单对话框
     * 该对话框会显示学生的信息，因此调用show之前必须设置current_student的值
     * @return 创建的对话框
     */
    private AlertDialog createItemMenuDialog() {
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bd = new Bundle();
                bd.putSerializable("student", current_student);
                it.putExtra("stu_bd", bd);
                startActivity(it);
            }
        });
        builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemDeleteConfirmDialog.show();
            }
        });
        builder.setTitle("学生详情");
        return builder.create();
    }

    /***
     * 创建删除确认对话框
     * @return 创建的的对话框
     * */
    private AlertDialog createConfirmDeleteDialog() {
        builder.setMessage(null);
        builder.setTitle("确认要删除吗？");
        builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeStudent(current_student);
            }
        });
        builder.setNegativeButton("取消", null);
        return builder.create();
    }

    private AlertDialog createExitDialog() {
        builder.setTitle("提示");
        builder.setMessage("确定要退出吗？");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    private AlertDialog createSearchDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("搜索");
        View view = LayoutInflater.from(this).inflate(R.layout.input_dialog, null);
        builder.setView(view);
        EditText input = view.findViewById(R.id.editText);
        builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input_text = input.getText().toString();
                Filter filter = adapter.getFilter(Student.class);
                filter.filter(input_text);
                if (input_text.length() > 0) {
                    search_filter.setTag(getText(R.string.search) + "  " + input_text);
                    search_filter.setText(search_filter.getTag().toString());
                    search_filter.setVisibility(View.VISIBLE);
                } else {
                    search_filter.setVisibility(View.GONE);
                }
                searchDialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                input.setText("");
                search_filter.setVisibility(View.GONE);
                Filter filter = adapter.getFilter(Student.class);
                filter.filter("");
                searchDialog.cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        exitDialog.show();
    }

    public void addNewStudent(Student student) {
        if (adapter.addOrModify(student)) {
            ImageToast.make(this, R.drawable.yes, "添加学生" + student.getStu_name() + "成功！").show();
        } else {
            ImageToast.make(this, R.drawable.yes, "修改学生" + student.getStu_name() + "成功！").show();
        }
    }

    public void removeStudent(Student student) {
        adapter.removeItem(current_student);
        ImageToast.make(this, R.drawable.yes, "删除学生" + student.getStu_name() + "成功！").show();
    }

    /**
     * start a new activity to record new student
     */
    public void recordNewStudent() {
        startActivity(new Intent(ActivityMain.this, ActivityStudent.class));
    }

}
