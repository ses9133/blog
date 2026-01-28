package org.example.blog.purchase;

import lombok.Data;
import org.example.blog._core.utils.DateUtil;

public class PurchaseResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private Long boardId;
        private String boardTitle;
        private String boardAuthor;
        private String purchasedAt;

        public ListDTO(Purchase purchase) {
            this.id = purchase.getId();
            if(purchase.getCreatedAt() != null) {
                this.purchasedAt = DateUtil.format(purchase.getCreatedAt());
            }
            if(purchase.getBoard() != null) {
                this.boardId = purchase.getBoard().getId();
                this.boardTitle = purchase.getBoard().getTitle();

                if(purchase.getBoard().getUser() != null) {
                    this.boardAuthor = purchase.getBoard().getUser().getUsername();
                }
            }
        }
    }
}
