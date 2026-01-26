package org.example.blog.payment;

import lombok.Data;
import org.example.blog._core.errors.exception.Exception400;

public class PaymentRequest {
    @Data
    public static class PrepareDTO {
        private Integer amount; // 충전할 포인트

        public void validate() {
            if(amount == null || amount < 0) {
                throw new Exception400("충전할 포인트는 0보다 커야합니다.");
            }

            if(amount < 100) {
                throw new Exception400("최소 충전 금액은 100포인트 입니다.");
            }

            if(amount > 100000) {
                throw new Exception400("최대 충전 금액을 100,000포인트 입니다.");
            }
        }
    }

    @Data // 결제 검증 요청 DTO
    public static class verifyDTO {
        private String paymentId;

        public void validate() {
            if(paymentId == null || paymentId.trim().isEmpty()) {
                throw new Exception400("결제 번호(주문 번호)가 필요합니다.");
            }
        }
    }
}
