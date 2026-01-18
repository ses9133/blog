package org.example.blog.board;

import lombok.Data;
import org.example.blog.user.User;

public class BoardRequest {
    @Data
    public static class SaveDTO {
        private String title;
        private String content;
        private Boolean premium;
        private String username;

        public void validate() {
            if(title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수입니다.");
            }
            if(content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다.");
            }
            if(username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("작성자는 필수입니다.");
            }
        }

        public Board toEntity(User user) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .premium(premium != null ? premium : false)
                    .user(user)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        private String title;
        private String content;
        private Boolean premium;

        public void validate() {
            if(title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수입니다.");
            }
            if(content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다.");
            }
        }
    }
}
