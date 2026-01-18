package org.example.blog._core.errors.exception;

/**
 * 404 Not Found 커스텀 예외 처리 클래스
 * */
public class Exception404 extends RuntimeException {
    public Exception404(String msg) {
        super(msg);
    }
}
