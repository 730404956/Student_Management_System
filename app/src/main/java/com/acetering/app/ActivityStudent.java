package com.acetering.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.acetering.app.bean.Student;
import com.acetering.student_input.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ActivityStudent extends Fragment {
    private View contentView;
    private EditText input_name;
    private Context context;
    private EditText input_stu_id;
    private TextView input_stu_birthday;
    private Student student;
    private Spinner select_colleagues;
    private Spinner select_majors;
    private RadioGroup select_sex;
    private AlertDialog dialog;
    private DatePickerDialog datePickerDialog;
    private String TAG = "stu_input";
    private Date birthday;
    private String[] colleague_names;
    private String[] major_default;
    private String[] major_cs;
    private String[] major_e;
    private ArrayAdapter<String> simple_major_adapter;

    private int counter = 0;

    public ActivityStudent() {
    }

    public ActivityStudent(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_spinner_names();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: student" + counter++);
        contentView = inflater.inflate(R.layout.activity_student, container, false);
        bindView();
        return contentView;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "resume");
        initData(student);
        super.onResume();
    }

    public void setStudent(Student student) {
        this.student = student;
        if (select_majors != null)
            initData(student);

    }

    private void showDatePicker() {
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(context);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    birthday = new Date();
                    Log.i(TAG, "onDateSet: " + dayOfMonth);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    birthday.setTime(calendar.getTimeInMillis());
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    input_stu_birthday.setText(sf.format(birthday));
                }
            });
        }
        datePickerDialog.show();
    }


    private void load_spinner_names() {
        Resources res = getResources();
        colleague_names = getResources().getStringArray(R.array.colleague_list);
        major_default = res.getStringArray(R.array.major_list_default);
        major_cs = res.getStringArray(R.array.major_list_CS);
        major_e = res.getStringArray(R.array.major_list_E);
    }

    private void initData(Student student) {
        if (student == null) {
            input_name.setText("");
            input_stu_id.setText("");
            input_stu_birthday.setText(getText(R.string.please_choose));
            select_colleagues.setSelection(0);
        } else {
            input_name.setText(student.getStu_name());
            input_stu_id.setText(student.getStu_id());
            if (student.getGender().equals(((RadioButton) select_sex.getChildAt(0)).getText().toString())) {
                ((RadioButton) select_sex.getChildAt(0)).setChecked(true);
            } else {
                ((RadioButton) select_sex.getChildAt(1)).setChecked(true);
            }
            birthday = student.getBirthday();
            input_stu_birthday.setText(new SimpleDateFormat("yyyy-MM-dd").format(student.getBirthday()));
            for (int i = 0; i < colleague_names.length; i++) {
                if (student.getColleague().equals(colleague_names[i])) {
                    select_colleagues.setSelection(i);
                    break;
                }
            }
        }
    }

    private void bindView() {
        input_name = contentView.findViewById(R.id.input_name);
        input_stu_id = contentView.findViewById(R.id.input_stu_id);
        select_sex = contentView.findViewById(R.id.select_sex);
        input_stu_birthday = contentView.findViewById(R.id.input_stu_birthday);
        select_colleagues = contentView.findViewById(R.id.select_colleague);
        select_majors = contentView.findViewById(R.id.select_major);
        Button btn_confirm = contentView.findViewById(R.id.btn_confirm);
        Button btn_cancel = contentView.findViewById(R.id.btn_cancel);
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            dialog = builder.create();
        }
        //bind listener for birthday select
        input_stu_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        //bind adapter for colleague spinner
        final ArrayAdapter<String> simple_colleague_adpter = new ArrayAdapter<>(context, R.layout.simple_string_spinner_item);
        simple_colleague_adpter.addAll(colleague_names);
        select_colleagues.setAdapter(simple_colleague_adpter);
        //bind adapter for major spinner
        simple_major_adapter = new ArrayAdapter<String>(context, R.layout.simple_string_spinner_item);
        simple_major_adapter.addAll(major_default);
        select_majors.setAdapter(simple_major_adapter);
        //connect content for colleague and major
        select_colleagues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private int previous_selection = 0;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == previous_selection) {
                    return;
                } else {
                    previous_selection = position;
                }
                simple_major_adapter.clear();
                switch (position) {
                    case 0:
                        simple_major_adapter.addAll(major_default);
                        break;
                    case 1:
                        simple_major_adapter.addAll(major_cs);
                        break;
                    case 2:
                        simple_major_adapter.addAll(major_e);
                        break;
                }
                if (position != 0 && student != null) {
                    for (int i = 0; i < simple_major_adapter.getCount(); i++) {
                        if (student.getMajor().equals(simple_major_adapter.getItem(i))) {
                            select_majors.setSelection(i);
                            break;
                        }
                    }
                } else {
                    select_majors.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //bind listener for btn_confirm
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input_name.getText().toString();
                if (name.length() == 0) {
                    dialog.setMessage("请输入姓名");
                    dialog.show();
                    input_name.requestFocus();
                    return;
                }
                String stu_id = input_stu_id.getText().toString();
                if (stu_id.length() == 0) {
                    dialog.setMessage("请输入学号");
                    dialog.show();
                    input_stu_id.requestFocus();
                    return;
                }
                if (birthday == null) {
                    dialog.setMessage("请选择生日");
                    dialog.show();
                    return;
                }
                if (select_colleagues.getSelectedItemPosition() == 0) {
                    dialog.setMessage("请选择学院");
                    dialog.show();
                    return;
                }
                if (select_majors.getSelectedItemPosition() == 0) {
                    dialog.setMessage("请选择专业");
                    dialog.show();
                    return;
                }
                String sex = String.valueOf(((RadioButton) contentView.findViewById(select_sex.getCheckedRadioButtonId())).getText());
                String colleague = select_colleagues.getSelectedItem().toString();
                String major = select_majors.getSelectedItem().toString();
                ViewManagerActivity.getInstance().addNewStudent(Student.copy(student, new Student(name, stu_id, sex, birthday, colleague, major)));
            }
        });
        //bind listener for btn_cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (student == null) {
                    input_name.setText("");
                    input_stu_id.setText("");
                    select_colleagues.setSelection(0);
                } else {
                    initData(student);
                }
            }
        });
    }

}
