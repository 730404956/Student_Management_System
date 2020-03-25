package com.acetering.app.dao;

import com.acetering.app.bean.User;

public class FakeUserDAO implements I_UserDAO {
    private String id = "";
    private String pwd = "";

    @Override
    public User login(String id, String password) {
        if (id.equals(this.id) && password.equals(this.pwd)) {
            return new User(id, password);
        } else {
            return null;
        }
    }
}
