package org.example.blog._core.utils;

import org.example.blog._core.errors.exception.Exception400;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(Timestamp timestamp) {
        if (timestamp == null) {
            throw new Exception400("timestamp는 비어있을 수 없습니다.");
        }
         return timestamp.toLocalDateTime().format(FORMATTER);
    }
}
