package org.example.blog.reply;

import lombok.Data;
import org.example.blog._core.utils.MyDateUtil;

public class ReplyResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String comment;
        private Long userId;
        private String username;
        private String createdAt;
        private boolean isOwner;

        public ListDTO(Reply reply, Long sessionUserId) {
            this.id = reply.getId();
            this.comment = reply.getComment();
            if(reply.getUser() != null) {
                this.userId = reply.getUser().getId();
                this.username = reply.getUser().getUsername();
            }
            if(reply.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.format(reply.getCreatedAt());
            }
            this.isOwner = reply.isOwner(sessionUserId);
        }
    }
}
