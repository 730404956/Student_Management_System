package com.acetering.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ActivityMain extends Fragment {
    private View contentView;
    private Context context;
    private ListView stu_list;
    private TextView search_filter;
    private FiltableAdapter<Student> adapter;

    private AlertDialog.Builder builder;
    private AlertDialog itemMenuDialog, itemDeleteConfirmDialog;
    private Student current_student;
    private int counter = 0;

    public ActivityMain() {
    }

    public ActivityMain(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        it = new Intent(ActivityMain.this, ActivityStudent.class);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("Activity Main", "onCreate: " + counter++);
        contentView = inflater.inflate(R.layout.activity_main, container, false);
        builder = new AlertDialog.Builder(context);
        itemMenuDialog = createItemMenuDialog();
        itemDeleteConfirmDialog = createConfirmDeleteDialog();
        bindView();
        return contentView;
    }


    /***
     * 绑定视图
     */
    private void bindView() {
        search_filter = contentView.findViewById(R.id.search_text);
        stu_list = contentView.findViewById(R.id.stu_list);
        ImageView btn_record = contentView.findViewById(R.id.btn_record_student);
        //新增学生
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordNewStudent();
            }
        });
        //显示适配器视图绑定配置
        adapter = new FiltableAdapter<Student>(context, ViewManagerActivity.getInstance().datas, R.layout.student_info_item) {
            @Override
            protected void bindView(ViewHolder holder, Student item) {
                String sex_male = getString(R.string.sex_male);
                holder.setText(R.id.stu_name, item.getStu_name())
                        .setText(R.id.stu_id, item.getStu_id())
                        .setText(R.id.stu_colleague, item.getColleague())
                        .setText(R.id.stu_major, item.getMajor())
                        .setImage(R.id.gender_img, context.getDrawable(item.getGender().equals(sex_male) ? R.drawable.male : R.drawable.female))
                        .setImage(R.id.stu_img, context.getDrawable(item.getGender().equals(sex_male) ? R.drawable.jobs : R.drawable.lena));
            }
        };
        adapter.setOnDataSetInvalid(new CallbackEvent() {
            @Override
            public void doJob(Context context) {
                ImageToast.make(context, R.drawable.no, "没有查询到任何学生的信息！").show();
            }
        });
        adapter.setOnResult(new CallbackEvent() {
            @Override
            public void doJob(Context context) {
                if (search_filter.getVisibility() != View.GONE)
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
                ViewManagerActivity.getInstance().changeToStudentFragment(current_student);
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
        builder.setTitle("确定要删除吗？");
        builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeStudent(current_student);
            }
        });
        builder.setNegativeButton("取消", null);
        return builder.create();
    }


    public void addNewStudent(Student student) {
        if (adapter.addOrModify(student)) {
            ImageToast.make(context, R.drawable.yes, "添加学生" + student.getStu_name() + "成功！").show();
        } else {
            ImageToast.make(context, R.drawable.yes, "修改学生" + student.getStu_name() + "成功！").show();
        }
    }

    public void removeStudent(Student student) {
        adapter.removeItem(current_student);
        ImageToast.make(context, R.drawable.yes, "删除学生" + student.getStu_name() + "成功！").show();
    }

    /**
     * start a new activity to record new student
     */
    public void recordNewStudent() {
        ViewManagerActivity.getInstance().changeToStudentFragment();
    }

    public void filter(String input_text) {
        Filter filter = adapter.getFilter(Student.class);
        filter.filter(input_text);
        if (input_text.length() > 0) {
            search_filter.setTag(getText(R.string.search) + "  " + input_text);
            search_filter.setText(search_filter.getTag().toString());
            search_filter.setVisibility(View.VISIBLE);
        } else {
            search_filter.setVisibility(View.GONE);
        }
    }

    public void clearFilter() {
        search_filter.setVisibility(View.GONE);
        Filter filter = adapter.getFilter(Student.class);
        filter.filter("");
    }
}
