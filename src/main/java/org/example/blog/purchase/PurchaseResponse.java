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
        private Integer price;
        private String purchasedAt;

        public ListDTO(Purchase purchase) {
            this.id = purchase.getId();
            this.price = purchase.getPrice();

            // 내가 구매한 일시 포맷팅
            if(purchase.getCreatedAt() != null) {
                this.purchasedAt = DateUtil.format(purchase.getCreatedAt());
            }

            // 평탄화
            // 조인 패치를 통해 한번에 들고오는 상태
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
