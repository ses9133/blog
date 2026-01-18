package org.example.blog._core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog._core.errors.exception.Exception401;
import org.example.blog.user.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        if(sessionUser == null) {
            throw new Exception401("로그인 먼저 해주세요");
        }

        return true;
    }
}
