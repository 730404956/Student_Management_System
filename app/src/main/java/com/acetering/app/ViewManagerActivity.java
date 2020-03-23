package com.acetering.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.acetering.student_input.R;

public class ViewManagerActivity extends AppCompatActivity implements View.OnClickListener {
    FragmentManager fManager;
    Fragment fragment_main, fragment_student;
    TextView t1, t2;

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


    }

    @Override
    public void onClick(View v) {
        Log.i("view manager", "onClick: " + v.isSelected());
        if (!v.isSelected()) {
            FragmentTransaction transaction = fManager.beginTransaction();
            if (v == t1) {
                t2.setSelected(false);
                t1.setSelected(true);
                if (fragment_main == null) {
                    fragment_main = new Fragment(R.layout.activity_main);
                    transaction.add(R.id.ly_content, fragment_main);
                } else {
                    transaction.show(fragment_main);
                }
                if (fragment_student != null)
                    transaction.hide(fragment_student);
            } else if (v == t2) {
                t2.setSelected(true);
                t1.setSelected(false);
                if (fragment_student == null) {
                    fragment_student = new Fragment(R.layout.activity_student);
                    transaction.add(R.id.ly_content, fragment_student);
                } else {
                    transaction.show(fragment_student);
                }
                if (fragment_main != null)
                    transaction.hide(fragment_main);
            }
            transaction.commit();
        }


    }
}
