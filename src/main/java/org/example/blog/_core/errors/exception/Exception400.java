package org.example.blog._core.errors.exception;

/**
 * 400 Bad Request 커스텀 예외 처리 클래스
 * */
public class Exception400 extends RuntimeException {
    public Exception400(String msg) {
        super(msg);
    }
}
