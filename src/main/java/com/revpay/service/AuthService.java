package com.revpay.service;

import com.revpay.db.UserDao;
import com.revpay.model.User;
import com.revpay.util.PasswordUtil;

public class AuthService {

    private final UserDao userDao = new UserDao();

    public User login(String emailOrPhone, String password) throws Exception {

        User user = userDao.findByEmailOrPhone(emailOrPhone);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        String hashedInput = PasswordUtil.hash(password);

        if (!hashedInput.equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid password.");
        }

        return user;
    }
}
