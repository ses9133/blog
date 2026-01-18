package org.example.blog._core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog.user.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null) {
            HttpSession session = request.getSession(false); // 세션있으면 가져오고 없으면 null 반환

            if(session != null) {
                User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
                modelAndView.addObject(SessionConstants.LOGIN_USER, sessionUser);
            }
        }
    }
}
