package org.example.blog._core.errors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blog._core.errors.exception.*;
import org.example.blog._core.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class MyExceptionHandler {

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api");
    }

    @ExceptionHandler(Exception400.class)
    @ResponseBody
    public ResponseEntity<?> ex400(Exception400 e, HttpServletRequest request) {
        log.warn("== 400 에러 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(e.getMessage()));
        }

        String script = "<script>" +
                "alert('" + e.getMessage() + "');" +
                "history.back();" +
                "</script>";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Exception401.class)
    @ResponseBody
    public ResponseEntity<?> ex401(Exception401 e, HttpServletRequest request) {
        log.warn("== 401 에러 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(e.getMessage()));
        }

        String script = "<script>" +
                "alert('" + e.getMessage() + "');" +
                "location.href = '/login'" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Exception403.class)
    @ResponseBody
    public ResponseEntity<?> ex403(Exception403 e, HttpServletRequest request) {
        log.warn("== 403 에러 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(e.getMessage()));
        }

        String script = "<script>alert('" + e.getMessage() + "');" +
                "history.back();" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }


    @ExceptionHandler(Exception404.class)
    @ResponseBody
    public ResponseEntity<?> ex404(Exception404 e, HttpServletRequest request) {
        log.warn("== 404 에러 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }

        String script = "<script>alert('" + e.getMessage() + "');" +
                "history.back();" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Exception500.class)
    @ResponseBody
    public ResponseEntity<?> ex500(Exception500 e, HttpServletRequest request) {
        log.warn("== 500 에러 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(e.getMessage()));
        }

        String script = "<script>alert('" + e.getMessage() + "');" +
                "history.back();" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<?> handleDataViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("== 데이터베이스 제약조건 위반 오류 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        String errorMessage = e.getMessage();
        String message;
        if (errorMessage != null && errorMessage.contains("FOREIGN KEY")) {
            message = "관련된 데이터가 있어 삭제할 수 없습니다.";
        } else {
            message = "데이터베이스 제약 조건 위반";
        }

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.fail(message));
        }

        String script = "<script>alert('" + message + "');" +
                "history.back();" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Error.class)
    @ResponseBody
    public ResponseEntity<?> handleError(Error e, HttpServletRequest request) {
        log.warn("== 클래스 로딩 오류 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        String message = "시스템내 오류 발생";

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(message));
        }

        String script = "<script>alert('" + message + "');" +
                "history.back();" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.warn("== 예상하지 못한 에러 발생 ==");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(e.getMessage()));
        }

        String script = "<script>alert('" + e.getMessage() + "');" +
                "history.back();" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }
}