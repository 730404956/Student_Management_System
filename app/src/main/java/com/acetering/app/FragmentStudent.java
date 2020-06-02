package com.acetering.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acetering.app.bean.Student;
import com.acetering.app.util.BitmapUtil;
import com.acetering.app.util.FileUtil;
import com.acetering.app.util.PermissionUtil;
import com.acetering.app.views.DialogFactory;
import com.acetering.app.views.DrawingPanelView;
import com.acetering.app.views.ImagePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


public class FragmentStudent extends Fragment {
    private View contentView;
    private EditText input_name;
    private Context context;
    private EditText input_stu_id;
    private EditText input_description;
    private TextView input_stu_birthday;
    private ImageView icon_img_view;
    private Student student;
    private Spinner select_colleagues;
    private Spinner select_majors;
    private RadioGroup select_sex;
    private AlertDialog errorDialog, imageSrcDialog;
    private Dialog paletteDialog;
    private DatePickerDialog datePickerDialog;
    private String TAG = "fragment student";
    private Date birthday;
    private String[] colleague_names;
    private String[] major_default;
    private String[] major_cs;
    private String[] major_e;
    private ImagePickerDialog imagePickerDialog;
    private ArrayAdapter<String> simple_major_adapter;
    private Bitmap image;

    //use for debug
    private int counter = 0;


    public FragmentStudent() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = ViewManagerActivity.getInstance();
        load_spinner_names();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: student " + counter++);
        if (savedInstanceState != null)
            student = (Student) savedInstanceState.get("student");
        contentView = inflater.inflate(R.layout.activity_student, container, false);
        bindView();
        ViewManagerActivity.getInstance().replaceStudentFragment(this);
        return contentView;
    }


    @Override
    public void onResume() {
        Log.i(TAG, "resume");
        initData(student);
        super.onResume();
    }

    private void setTitle() {
        if (student != null && student.getStu_name().length() > 0) {
            ((Activity) context).setTitle(getString(R.string.edit) + student.getStu_name());
        } else {
            ((Activity) context).setTitle(R.string.app_name);
        }
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        String name = input_name.getText().toString();
        String stu_id = input_stu_id.getText().toString();
        String sex = String.valueOf(((RadioButton) contentView.findViewById(select_sex.getCheckedRadioButtonId())).getText());
        String colleague = select_colleagues.getSelectedItem().toString();
        String major = select_majors.getSelectedItem().toString();
        String description = input_description.getText().toString();
        student = Student.copy(student, new Student(name, stu_id, sex, birthday, colleague, major, description));
        if (image != null) {
            student.setImage(image);
        }
        super.onPause();
    }

    public void setStudent(Student student) {
        this.student = student;
        if (select_majors != null)
            initData(student);
    }

    private void showDatePicker() {
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(context);
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                birthday = new Date();
                Log.i(TAG, "onDateSet: " + dayOfMonth);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                birthday.setTime(calendar.getTimeInMillis());
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                input_stu_birthday.setText(sf.format(birthday));
            });
        }
        datePickerDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("student", student);
        super.onSaveInstanceState(outState);
    }

    private void load_spinner_names() {
        Resources res = getResources();
        colleague_names = res.getStringArray(R.array.colleague_list);
        major_default = res.getStringArray(R.array.major_list_default);
        major_cs = res.getStringArray(R.array.major_list_CS);
        major_e = res.getStringArray(R.array.major_list_E);
    }

    private void initData(Student student) {
        setTitle();
        if (student == null) {
            clearData();
        } else {
            image = BitmapUtil.bytesToBitmap(student.getImage());
            if (image == null) {
                icon_img_view.setImageDrawable(getResources().getDrawable(R.drawable.jobs, null));
            } else {
                icon_img_view.setImageDrawable(new BitmapDrawable(getResources(), image));
            }
            input_name.setText(student.getStu_name());
            input_stu_id.setText(student.getStu_id());
            if (student.getGender().equals(((RadioButton) select_sex.getChildAt(0)).getText().toString())) {
                ((RadioButton) select_sex.getChildAt(0)).setChecked(true);
            } else {
                ((RadioButton) select_sex.getChildAt(1)).setChecked(true);
            }
            birthday = student.getBirthday();
            if (birthday == null) {
                input_stu_birthday.setText(getText(R.string.please_choose));
            } else {
                input_stu_birthday.setText(new SimpleDateFormat("yyyy-MM-dd").format(student.getBirthday()));
            }
            for (int i = 0; i < colleague_names.length; i++) {
                if (student.getColleague().equals(colleague_names[i])) {
                    select_colleagues.setSelection(i);
                    break;
                }
            }
            input_description.setText(student.getDescription());
        }
    }

    private void clearData() {
        icon_img_view.setImageDrawable(getResources().getDrawable(R.drawable.add_icon, null));
        input_name.setText("");
        input_stu_id.setText("");
        input_stu_birthday.setText(getText(R.string.please_choose));
        birthday = null;
        select_colleagues.setSelection(0);
        input_description.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FileUtil.FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    //get file path
                    String path = Uri.decode(data.getDataString()).substring(Uri.decode(data.getDataString()).indexOf("raw:") + 4);
                    //read file
                    String content = FileUtil.ReadTxtFile(path);
                    if (student == null) {
                        student = new Student();
                    }
                    student.setDescription(content);
                    Log.i(TAG, "onActivityResult: " + content);
                    Toast.makeText(context, "导入信息成功！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showImagePickerDialog() {
        if (imagePickerDialog == null) {
            ImagePickerDialog.Builder builder = new ImagePickerDialog.Builder(context);
            builder.setPositiveButton(getString(R.string.confirm), v1 -> {
                if (builder.mDialog.getChoosen_img() != null) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), builder.mDialog.getChoosen_img());
                    icon_img_view.setImageDrawable(bitmapDrawable);
                    image = builder.mDialog.getChoosen_img();
                }
            });
            imagePickerDialog = builder.create();
        }
        imagePickerDialog.show();
    }

    private void showImageSrcDialog() {
        if (imageSrcDialog == null) {
            imageSrcDialog = DialogFactory.createChooseDialog(context, "请选择头像来源", new String[]{"从相册选择", "绘制新头像"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        showImagePickerDialog();
                        break;
                    case 1:
                        showPaletteDialog();
                        break;
                }
            });
        }
        imageSrcDialog.show();
    }

    private void showPaletteDialog() {
        if (paletteDialog == null) {
            paletteDialog = new DrawingPanelView(context, ((cxt, data) -> {
                image = BitmapUtil.zoomImg((Bitmap) data, 96, 96);
                icon_img_view.setImageDrawable(new BitmapDrawable(getResources(), image));
            }));
        }
        paletteDialog.show();
    }

    private void bindView() {
        input_name = contentView.findViewById(R.id.input_name);
        input_stu_id = contentView.findViewById(R.id.input_stu_id);
        select_sex = contentView.findViewById(R.id.select_sex);
        input_stu_birthday = contentView.findViewById(R.id.input_stu_birthday);
        select_colleagues = contentView.findViewById(R.id.select_colleague);
        select_majors = contentView.findViewById(R.id.select_major);
        input_description = contentView.findViewById(R.id.description);
        icon_img_view = contentView.findViewById(R.id.choose_img);
        //set listener for image picker
        icon_img_view.setOnClickListener(v -> {
            showImageSrcDialog();
        });
        contentView.findViewById(R.id.load_data).setOnClickListener(v -> {
            PermissionUtil util = PermissionUtil.getInstance(context);
            if (util.checkReadPermission()) {
                startActivityForResult(FileUtil.getInstance(context).getFileChooserIntent(), FileUtil.FILE_SELECT_CODE);
            } else {
                util.getReadPermission();
            }
        });
        Button btn_confirm = contentView.findViewById(R.id.btn_confirm);
        Button btn_cancel = contentView.findViewById(R.id.btn_cancel);
        if (errorDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            errorDialog = builder.create();
        }
        //bind listener for birthday select
        input_stu_birthday.setOnClickListener(v -> showDatePicker());
        //bind adapter for colleague spinner
        final ArrayAdapter<String> simple_colleague_adpter = new ArrayAdapter<>(context, R.layout.simple_string_spinner_item);
        simple_colleague_adpter.addAll(colleague_names);
        select_colleagues.setAdapter(simple_colleague_adpter);
        //bind adapter for major spinner
        simple_major_adapter = new ArrayAdapter<>(context, R.layout.simple_string_spinner_item);
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
        btn_confirm.setOnClickListener(v -> {
            String name = input_name.getText().toString();
            if (name.length() == 0) {
                errorDialog.setMessage("请输入姓名");
                errorDialog.show();
                input_name.requestFocus();
                return;
            }
            String stu_id = input_stu_id.getText().toString();
            if (stu_id.length() == 0) {
                errorDialog.setMessage("请输入学号");
                errorDialog.show();
                input_stu_id.requestFocus();
                return;
            }
            if (birthday == null) {
                errorDialog.setMessage("请选择生日");
                errorDialog.show();
                return;
            }
            if (select_colleagues.getSelectedItemPosition() == 0) {
                errorDialog.setMessage("请选择学院");
                errorDialog.show();
                return;
            }
            if (select_majors.getSelectedItemPosition() == 0) {
                errorDialog.setMessage("请选择专业");
                errorDialog.show();
                return;
            }
            String sex = String.valueOf(((RadioButton) contentView.findViewById(select_sex.getCheckedRadioButtonId())).getText());
            String colleague = select_colleagues.getSelectedItem().toString();
            String major = select_majors.getSelectedItem().toString();
            String description = input_description.getText().toString();
            Student r_student = new Student(name, stu_id, sex, birthday, colleague, major, description);
            r_student.setImage(image);
            ViewManagerActivity.getInstance().addNewStudent(Student.copy(student, r_student));
            ViewManagerActivity.getInstance().changeToMainFragment();
        });
        //bind listener for btn_cancel
        btn_cancel.setOnClickListener(v -> {
            if (student == null) {
                clearData();
            } else {
                initData(student);
            }
        });
    }
}
