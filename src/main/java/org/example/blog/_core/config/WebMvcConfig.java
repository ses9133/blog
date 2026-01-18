package org.example.blog._core.config;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.interceptor.AdminInterceptor;
import org.example.blog._core.interceptor.LoginInterceptor;
import org.example.blog._core.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final SessionInterceptor sessionInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                        .addPathPatterns("/**");

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/board/**", "/user/**", "/reply/**", "/admin/**")
                .excludePathPatterns(
                        "/login/**",
                        "/join",
                        "/logout",
                        "/user/kakao",
                        "/user/naver",
                        "/board/list",
                        "/",
                        "/board/{id:\\d+}",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.io",
                        "/h2-console/**"
                );

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/uploads/");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
