package com.acetering.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.acetering.app.bean.User;
import com.acetering.app.dao.FakeUserDAO;
import com.acetering.app.dao.I_UserDAO;
import com.acetering.student_input.R;


public class ActivityLogin extends AppCompatActivity {
    I_UserDAO userDAO;
    TextView input_id;
    TextView input_pwd;
    AlertDialog.Builder builder;
    AlertDialog pgDialog;
    AlertDialog wrongDialog;
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        userDAO = new FakeUserDAO();
        builder = new AlertDialog.Builder(ActivityLogin.this);
        pgDialog = createLoginProgressDialog();
        wrongDialog = createLoginWrongDialog();
        handler = new LoginHandler(this);
        bindView();
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
                            sleep(100);
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
        //加载布局
        final LayoutInflater inflater = ActivityLogin.this.getLayoutInflater();
        View view_custom = inflater.inflate(R.layout.progressbar_dialog, null, false);
        //设置标题
        builder.setTitle("登陆中……");
        //取消设置消息内容
        builder.setMessage(null);
        //设置布局
        builder.setView(view_custom);
        //设置不可取消
        builder.setCancelable(false);
        return builder.create();
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
                    context.startActivity(new Intent(context, ActivityMain.class));
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
