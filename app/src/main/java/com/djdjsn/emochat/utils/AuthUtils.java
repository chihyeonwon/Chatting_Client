package com.djdjsn.emochat.utils;

import java.util.Locale;
import java.util.Random;

public class AuthUtils {

    public static String emailize(String id) {
        return String.format(Locale.getDefault(), "%s@emochat.com", id);
    }

    public static String generateVerificationCode() {
        int rand = 1000 + new Random(System.currentTimeMillis()).nextInt(9000);
        return String.valueOf(rand);
    }

}
