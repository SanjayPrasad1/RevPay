package com.revpay.service;

import com.revpay.db.UserDao;
import com.revpay.model.User;
import com.revpay.util.PasswordUtil;

import java.time.LocalDateTime;

public class UserService {

    private final UserDao userDao = new UserDao();

    public void register(String fullName, String email, String phone, String password, String pin) throws Exception {

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setPinHash(PasswordUtil.hash(pin));
        user.setUserType("PERSONAL");
        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        user.setCreatedAt(LocalDateTime.now());

        userDao.save(user);
    }
}
