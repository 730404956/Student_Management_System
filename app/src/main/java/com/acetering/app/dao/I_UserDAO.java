package com.acetering.app.dao;

import com.acetering.app.bean.User;

public interface I_UserDAO {
    User login(String id, String password);
}
