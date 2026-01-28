package org.example.blog.user;

import lombok.Data;
import org.example.blog._core.errors.exception.Exception400;
import org.springframework.web.multipart.MultipartFile;

public class UserRequest {
    @Data
    public static class LoginDTO {
        private String username;
        private String password;

        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자명을 입력해주세요");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호를 입력해주세요");
            }
        }
    }

    @Data
    public static class JoinDTO {
        private String username;
        private String password;
        private String email;
        private MultipartFile profileImage;

        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new Exception400("사용자명을 입력해주세요");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new Exception400("비밀번호를 입력해주세요");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new Exception400("이메일을 입력해주세요");
            }
            if (!email.contains("@")) {
                throw new Exception400("올바른 이메일 형식이 아닙니다.");
            }
        }

        public User toEntity(String profileImageFileName) {
            return User.builder()
                    .username(this.username)
                    .password(this.password)
                    .email(this.email)
                    .role(Role.USER)
                    .provider(OAuthProvider.LOCAL)
                    .profileImage(profileImageFileName)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        private String password;
        private MultipartFile profileImage;
        private String profileImageFileName;

        public void validate() {
            if (password == null || password.trim().isEmpty()) {
                throw new Exception400("비밀번호를 입력해주세요");
            }
            if (password.length() < 4) {
                throw new Exception400("비밀번호는 4자리 이상이어야 합니다.");
            }
        }
    }

    @Data
    public static class EmailCheckDTO {
        private String email;
        private String code;

        public void validate() {
            if (email == null || email.trim().isEmpty()) {
                throw new Exception400("이메일을 입력해주세요");
            }
            if (!email.contains("@")) {
                throw new Exception400("올바른 이메일 형식이 아닙니다.");
            }
        }
    }
}
