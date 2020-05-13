package com.acetering.otherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.acetering.app.IDayOfWeek;

public class AIDLServiceTestActivity extends AppCompatActivity {
    IDayOfWeek iPerson;
    private PersonConnection conn = new PersonConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_i_d_l_service_test);

        Intent service = new Intent("com.acetering.student_input.QueryWeekdayService");
        service.setPackage("com.acetering.app");

        if (!bindService(service, conn, BIND_AUTO_CREATE)) {
            Log.i("FUCK", "onCreate: 失败");
        }
        Button btn_confirm = findViewById(R.id.btn_confirm);
        final EditText year = findViewById(R.id.year);
        final EditText month = findViewById(R.id.month);
        final EditText day = findViewById(R.id.day);
        final TextView dow = findViewById(R.id.day_of_week);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day_of_week = -1;
                try {
                    day_of_week = iPerson.getWeekday(Integer.parseInt(year.getText().toString()), Integer.parseInt(month.getText().toString()) - 1, Integer.parseInt(day.getText().toString()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                dow.setText("星期" + new String[]{"日", "一", "二", "三", "四", "五", "六"}[day_of_week - 1]);
            }
        });
    }

    private final class PersonConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {

            iPerson = IDayOfWeek.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName name) {
            iPerson = null;
        }
    }
}
