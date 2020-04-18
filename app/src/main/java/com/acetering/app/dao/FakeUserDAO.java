package com.acetering.app.dao;

import android.content.Context;
import android.preference.PreferenceManager;

import com.acetering.app.bean.User;

public class FakeUserDAO implements I_UserDAO {
    private static FakeUserDAO instance;
    private Context context;
    private String mid = "";
    private String mpwd = "";

    private FakeUserDAO(Context context) {
        this.context = context;
    }

    public static FakeUserDAO getInstance(Context context) {
        if (instance == null) {
            instance = new FakeUserDAO(context);
            instance.mid = PreferenceManager.getDefaultSharedPreferences(context).getString("account_id", "");
            instance.mpwd = PreferenceManager.getDefaultSharedPreferences(context).getString("account_password", "");
        }
        return instance;
    }

    @Override
    public User login(String id, String password) {

        if (id.equals(mid) && password.equals(mpwd)) {
            return new User(id, password);
        } else {
            return null;
        }
    }

    public void update() {
        instance.mid = PreferenceManager.getDefaultSharedPreferences(context).getString("account_id", "");
        instance.mpwd = PreferenceManager.getDefaultSharedPreferences(context).getString("account_password", "");
    }
}
