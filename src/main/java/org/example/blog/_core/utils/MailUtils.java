package org.example.blog._core.utils;

import java.util.Random;

public class MailUtils {
    public static String generateRandomCode() {
        Random random = new Random();

        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
