package com.revpay.service;

import com.revpay.db.UserDao;
import com.revpay.model.User;
import com.revpay.util.PasswordUtil;

import java.time.LocalDateTime;

public class AuthService {

    private final UserDao userDao = new UserDao();

    public User login(String emailOrPhone, String password) throws Exception {

        User user = userDao.findByEmailOrPhone(emailOrPhone);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())){
            throw new RuntimeException("Account locked. Try again later.");
        }

        String hashedInput = PasswordUtil.hash(password);

        if (!hashedInput.equals(user.getPasswordHash())) {

            int newAttempts = user.getFailedAttempts()+1;
            LocalDateTime lockUntil = null;

            if (newAttempts >= 3){
                lockUntil = LocalDateTime.now().plusMinutes(3);
                newAttempts = 0; //reset counter after lock
            }

            userDao.updateFailedAttempts(user.getId(), newAttempts, lockUntil);

            throw new RuntimeException("Invalid password.");
        }

        userDao.updateFailedAttempts(user.getId(), 0, null);

        return user;
    }
    public void verifyPin(long userId, String pin) throws Exception {

        User user = userDao.findById(userId);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

//        System.out.println("DEBUG stored pin hash = " + user.getPinHash());
//        System.out.println("DEBUG input pin hash = " + PasswordUtil.hash(pin));

        if (user.getPinLockedUntil() != null &&
                user.getPinLockedUntil().isAfter(LocalDateTime.now())) {

            throw new RuntimeException("PIN locked. Try again later.");
        }

        String hashedPin = PasswordUtil.hash(pin);

        if (!hashedPin.equals(user.getPinHash())) {

            int newAttempts = user.getPinFailedAttempts() + 1;
            LocalDateTime lockUntil = null;

            if (newAttempts >= 3) {
                lockUntil = LocalDateTime.now().plusMinutes(1);
                newAttempts = 0;
            }

            userDao.updatePinFailedAttempts(user.getId(), newAttempts, lockUntil);

            throw new RuntimeException("Invalid PIN.");
        }

        // successful PIN → reset
        userDao.updatePinFailedAttempts(user.getId(), 0, null);
    }

}
