package org.example.blog.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

public class UserResponse {
    /**
     * 회원 정보 수정 화면 DTO
     */
    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String username;
        private String email;

        public UpdateFormDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String username;
        private String email;
        private String profileImage;

        public DetailDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.profileImage = user.getProfileImage() == null ? null : user.getProfileImage();
        }
    }

    /**
     * 로그인 응답 DTO (세션 저장용)
     * - 세션에 엔티티 정보를 저장하지만 다른 곳으로 전달할 때는 DTO를 사용하는 것이 권장 사항
     */
    @Data
    public static class LoginDTO {
        private Long id;
        private String username;
        private String email;

        public LoginDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }

    // JWT DTO 설계
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    // @JsonNaming: 자바 객체의 필드명 <-> JSON(스네이크 표기법) 자동 변환
    @Data
    public static class OAuthToken {
        private String tokenType;
        private String accessToken;
        private Integer expiresIn;
        private String refreshToken;
        private Integer refreshTokenExpiresIn;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class KaKaoProfile {
        private Long id;
        private String connectedAt;
        private Properties properties;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class Properties {
        private String nickname;
        private String profileImage;
        private String thumbnailImage;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class NaverProfile {
        private String resultCode;
        private String message;
        private Response response;

        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
        @Data
        public static class Response {
            private String id;
            private String name;
            private String email;
            private String profileImage;
        }
    }
}
