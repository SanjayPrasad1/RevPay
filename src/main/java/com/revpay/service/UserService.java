package com.revpay.service;

import com.revpay.db.DBConnection;
import com.revpay.db.UserDao;
import com.revpay.db.WalletDao;
import com.revpay.model.User;
import com.revpay.util.PasswordUtil;

import java.sql.Connection;
import java.time.LocalDateTime;

public class UserService {

    private final UserDao userDao = new UserDao();
    private final WalletService walletService = new WalletService();

    private final WalletDao walletDao = new WalletDao();

    public void register(String fullName, String email, String phone, String password, String pin) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
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

                long userId = userDao.save(user, con);

                walletDao.createForUser(userId, con);

                con.commit();

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }
}
