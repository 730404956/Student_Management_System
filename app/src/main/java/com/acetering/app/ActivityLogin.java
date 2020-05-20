package com.acetering.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.acetering.app.bean.User;
import com.acetering.app.dao.FakeUserDAO;
import com.acetering.app.dao.I_UserDAO;
import com.acetering.app.util.AppConfig;
import com.acetering.app.views.DialogFactory;



public class ActivityLogin extends AppCompatActivity {
    I_UserDAO userDAO;
    TextView input_id;
    TextView input_pwd;
    AlertDialog.Builder builder;
    AlertDialog pgDialog;
    AlertDialog wrongDialog;
    AlertDialog adsDialog;
    static Handler handler;
    String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PackageManager pm = getPackageManager();
        pm.queryBroadcastReceivers(new Intent("CLIPBOARD_MONITOR_ADD_STUDENT"), 0);
        setTitle(R.string.login);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String id = sp.getString("account_id", "");
        String password = sp.getString("account_password", "");
        userDAO = FakeUserDAO.getInstance(getBaseContext());
        builder = new AlertDialog.Builder(ActivityLogin.this);
        pgDialog = createLoginProgressDialog();
        wrongDialog = createLoginWrongDialog();
        handler = new LoginHandler(this);
        bindView();
        input_id.setText(id);
        input_pwd.setText(password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppConfig.applyConfig(this);
        Log.i(TAG, "onStart: ");
    }

    private void bindView() {
        input_id = findViewById(R.id.login_id);

        input_pwd = findViewById(R.id.login_pwd);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgDialog.show();
                //新开线程处理登录
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //登录
                            User user = userDAO.login(input_id.getText().toString(), input_pwd.getText().toString());
                            //模拟休眠3秒
                            sleep(1000);
                            pgDialog.cancel();
                            if (user != null) {//账户密码核验成功
                                handler.sendEmptyMessage(0);
                            } else {//账户密码核验失败
                                handler.sendEmptyMessage(3);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    private AlertDialog createLoginProgressDialog() {
        pgDialog = DialogFactory.createProgressDialog(this, "登陆中...");
        return pgDialog;
    }

    private void showAdsDialog() {
        if (adsDialog == null) {
            adsDialog = DialogFactory.createAdsDialog(this, () -> {
                startActivity(new Intent(ActivityLogin.this, ViewManagerActivity.class));
                adsDialog.cancel();
                finish();
            }, null, 10);
        }
        adsDialog.show();
    }

    private AlertDialog createLoginWrongDialog() {
        //取消设置的view
        builder.setView(null);
        //设置标题
        builder.setTitle("登陆出错");
        //设置消息内容
        builder.setMessage("用户名或密码错误！");
        //设置可以取消
        builder.setCancelable(true);
        return builder.create();
    }


    private static class LoginHandler extends Handler {
        ActivityLogin context;

        LoginHandler(ActivityLogin context) {
            super();
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            context.pgDialog.cancel();
            switch (msg.what) {
                case 0://登录成功
                    context.showAdsDialog();

                    break;
                case 1://账号不存在
                case 2://账号和密码不匹配
                case 3://登陆失败，未知原因
                    context.wrongDialog.show();
                    break;
            }
        }
    }
}
