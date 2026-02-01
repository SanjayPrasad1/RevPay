package com.revpay.util;

import org.mindrot.jbcrypt.BCrypt;
public class PasswordUtil {

//    public static String hash(String input) {
//        return "HASHED_" + input;
//    }
public static String hash(String raw) {
    return BCrypt.hashpw(raw, BCrypt.gensalt());
}

    public static boolean verify(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }

}
