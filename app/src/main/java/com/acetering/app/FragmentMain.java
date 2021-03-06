package com.acetering.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.acetering.app.adapter.BasicAdapter;
import com.acetering.app.adapter.FiltableAdapter;
import com.acetering.app.adapter.SQLiteFilterableAdapter;
import com.acetering.app.bean.Student;
import com.acetering.app.util.BitmapUtil;
import com.acetering.app.util.SoundUtil;
import com.acetering.app.views.ImageToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * Author:Acetering
 * Last Update:2020/3/25
 */
public class FragmentMain extends Fragment {
    private View contentView;
    private Context context;
    private ListView stu_list;
    private TextView search_filter;
    private FiltableAdapter<Student> adapter;
    private String filter_key_words;

    private MediaPlayer soundPlayer_add, soundPlayer_delete;

    private AlertDialog.Builder builder;
    private AlertDialog itemMenuDialog, itemDeleteConfirmDialog;
    private Student current_student;
    private boolean play_default_add, play_default_delete;

    public FragmentMain() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.context = ViewManagerActivity.getInstance();
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get content view
        contentView = inflater.inflate(R.layout.activity_main, container, false);
        //create dialog builder
        builder = new AlertDialog.Builder(context);
        //create item long click dialog
        itemMenuDialog = createItemMenuDialog();
        //create delete confirm dialog
        itemDeleteConfirmDialog = createConfirmDeleteDialog();
        //init view
        bindView();
        //get filter keywords to load filter result
        if (savedInstanceState != null) {
            filter_key_words = (String) savedInstanceState.get("filter_key_words");
        }
        //reload filter result after back to front
        adapter.getFilter(Student.class).filter(filter_key_words);
        //replace instance of view manager
        ViewManagerActivity.getInstance().replaceMainFragment(this);
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        play_default_add = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound_add", true);
        play_default_delete = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound_delete", true);
        ((Activity) context).setTitle(R.string.app_name);
    }

    /***
     * 绑定视图
     */
    private void bindView() {
        Log.i("main", "bindView: ");
        //get search_filter info from xml
        search_filter = contentView.findViewById(R.id.search_text);
        //get list view from xml
        stu_list = contentView.findViewById(R.id.stu_list);
        //显示适配器视图绑定配置
        adapter = new SQLiteFilterableAdapter(context, null, R.layout.student_info_item, (BasicAdapter.ViewBinder<Student>) (holder, item) -> {
            String sex_male = getString(R.string.sex_male);
            holder.setText(R.id.stu_name, item.getStu_name())
                    .setText(R.id.stu_id, item.getStu_id())
                    .setText(R.id.stu_colleague, item.getColleague())
                    .setText(R.id.stu_major, item.getMajor())
                    .setImage(R.id.gender_img, context.getDrawable(item.getGender().equals(sex_male) ? R.drawable.male : R.drawable.female));
            if (item.getImage() == null) {
                holder.setImage(R.id.stu_img, context.getDrawable(item.getGender().equals(sex_male) ? R.drawable.jobs : R.drawable.lena));
            } else {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), BitmapUtil.bytesToBitmap(item.getImage()));
                holder.setImage(R.id.stu_img, drawable);
            }

        });
        //set message when filter result of nothing
        adapter.setOnDataSetInvalid((context, object) -> ImageToast.make(context, R.drawable.no, "没有查询到任何学生的信息！").show());
        //set listener for filter finished
        adapter.setOnResult((context, object) -> {
            //update text in search info
            showSearchFilter(filter_key_words);
        });
        //设置列表项监听
        stu_list.setOnItemLongClickListener((parent, view, position, id) -> {
            //获取选中的学生对象
            current_student = adapter.getItem(position);
            //更新对话框显示文本
            itemMenuDialog.setMessage(current_student.toString());
            //显示菜单
            itemMenuDialog.show();
            return false;
        });
        //set adapter for list view
        stu_list.setAdapter(adapter);
    }

    /***
     * 创建列表项菜单对话框
     * 该对话框会显示学生的信息，因此调用show之前必须设置current_student的值
     * @return 创建的对话框
     */
    private AlertDialog createItemMenuDialog() {
        //do something after click 'edit'
        builder.setPositiveButton(R.string.edit, (dialog, which) -> {
            //turn to change student info view
            ViewManagerActivity.getInstance().changeToStudentFragment(current_student);
        });
        //do something after click 'delete'
        builder.setNegativeButton(R.string.remove, (dialog, which) -> itemDeleteConfirmDialog.show());
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
        playSound_add();
    }

    public void removeStudent(Student student) {
        adapter.removeItem(current_student);
        ImageToast.make(context, R.drawable.yes, "删除学生" + student.getStu_name() + "成功！").show();
        playSound_delete();
    }

    /**
     * start a new activity to record new student
     */
    public void recordNewStudent() {
        ViewManagerActivity.getInstance().changeToStudentFragment();
    }

    public void filter(String input_text) {
        filter_key_words = input_text;
        Filter filter = adapter.getFilter(Student.class);
        filter.filter(input_text);

    }

    private void showSearchFilter(String keywords) {
        if (keywords == null)
            return;
        if (keywords.length() > 0) {
            search_filter.setVisibility(View.VISIBLE);
            search_filter.setText(getText(R.string.search) + "  \"" + keywords + "\"" + " 共有" + adapter.getCount() + "条结果。");
        } else {
            search_filter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("filter_key_words", filter_key_words);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.close();
    }

    public void clearFilter() {
        filter_key_words = "";
        Filter filter = adapter.getFilter(Student.class);
        filter.filter(filter_key_words);
    }

    private void playSound_add() {
        if (play_default_add) {
            if (soundPlayer_add == null)
                soundPlayer_add = MediaPlayer.create(context, R.raw.new_student);
            soundPlayer_add.start();
        } else {
            SoundUtil.play(context, "add.mp3");
        }
    }

    private void playSound_delete() {
        if (!play_default_delete) {
            SoundUtil.play(context, "delete.mp3");
        } else {
            if (soundPlayer_delete == null)
                soundPlayer_delete = MediaPlayer.create(context, R.raw.delete_student);
            soundPlayer_delete.start();
        }

    }
}
