package org.example.blog._core.errors.exception;

/**
 * 401 UnAuthorized 인증 처리 오류
 * */
public class Exception401 extends RuntimeException {
    public Exception401(String msg) {
        super(msg);
    }
}
