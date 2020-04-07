package com.acetering.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acetering.app.bean.TelNumber;
import com.acetering.app.util.HttpUtil;
import com.acetering.app.util.TelInfoQuery;
import com.acetering.app.views.DialogFactory;
import com.acetering.student_input.R;
import com.google.gson.Gson;

public class ActivityPhonePlace extends AppCompatActivity {
    private Button query_btn;
    private Button cancel_btn;
    private EditText input_tel;
    private String TAG = "TEL_QUERY";
    private TelInfoQuery query;
    private boolean waitForResult = true;
    private AlertDialog errorDialog, successDialog, pgDialog;
    private HttpUtil.HttpResponse response;

    class TelResponse implements HttpUtil.HttpResponse {
        private Gson gson = new Gson();

        @Override
        public void onSuccess(String responseString) {
            TelNumber telNumber = gson.fromJson(responseString, TelNumber.class);
            String province = telNumber.getProvince();
            if (province == null) {
                showErrorDialog("查询失败！请检查号码是否正确！");
                return;
            }
            if (waitForResult) {
                pgDialog.cancel();
                showSuccessDialog(telNumber);
            }
        }

        @Override
        public void onFail(String error) {
            showErrorDialog(error);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_place);
        response = new TelResponse();
        query = new TelInfoQuery(response);
        bindView();
    }

    private void bindView() {
        query_btn = findViewById(R.id.btn_query);
        cancel_btn = findViewById(R.id.btn_cancel);
        input_tel = findViewById(R.id.input_tel);

        query_btn.setOnClickListener(v -> {
            String tel = input_tel.getText().toString();
            if (tel.length() != 11) {
                showErrorDialog("电话号码格式不正确！");
            } else {
                showProgressDialog();
                query.searchPhoneInfo(tel);
            }
        });
    }

    private void showSuccessDialog(TelNumber telNumber) {
        if (successDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.success));
            successDialog = builder.create();
            successDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.copy) + getString(R.string.tel_province), (dialog, witch) -> {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("tel province", telNumber.getProvince());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(ActivityPhonePlace.this, "已将归属地复制到剪贴板", Toast.LENGTH_SHORT).show();
            });
        }
        successDialog.setMessage(telNumber.toString());
        successDialog.show();
    }


    private void showErrorDialog(String msg) {
        if (errorDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.error));
            errorDialog = builder.create();
        }
        errorDialog.setMessage(msg);
        errorDialog.show();
    }

    private void showProgressDialog() {
        if (pgDialog == null) {
            pgDialog = DialogFactory.createProgressDialog(this, "查询中...");
        }
        waitForResult = true;
        pgDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, witch) -> {
            pgDialog.cancel();
            waitForResult = false;
        });
        pgDialog.show();
    }

}
