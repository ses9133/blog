package org.example.blog.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.example.blog._core.utils.DateUtil;

public class PaymentResponse {
    @Data
    public static class PrepareDTO {
        private String paymentId;
        private Integer amount;

        public PrepareDTO(String paymentId, Integer amount) {
            this.paymentId = paymentId;
            this.amount = amount;
        }
    }

    @Data
    public static class VerifyDTO {
        private Integer amount;
        private Integer currentPoint;

        public VerifyDTO(Integer amount, Integer currentPoint) {
            this.amount = amount;
            this.currentPoint = currentPoint;
        }
    }

    // 포트원 결제 조회 응답 DTO
    @Data
    public static class PortOneV2Response {
        private String status;          // "PAID" (대문자)
        private String id;              // "B73219e32" (paymentId)
        private String orderName;       // "포인트 충전"  TODO - 이름 바꿔야함
        private Amount amount;
        private String paidAt;

        @Data
        public static class Amount {
            private Integer total;
        }

    }

    @Data
    public static class ListDTO {
        private Long id;
        private String paymentId;
        private Integer amount;
        private String paidAt;
        private PaymentStatus paymentStatus;
        private String statusDisplay;

        private Boolean isRefundable;

        public ListDTO(Payment payment, Boolean isRefundable) {
            this.id = payment.getId();
            this.paymentId = payment.getPaymentId();
            this.amount = payment.getAmount();
            this.paymentStatus = payment.getPaymentStatus();
            this.isRefundable = isRefundable != null ? isRefundable : false;

            if (payment.isPaid()) {
                this.statusDisplay = "결제완료";
            } else if(payment.isCancelled()) {
                this.statusDisplay = "환불완료";
            } else {
                this.statusDisplay = "결제대기";
            }

            if (payment.getCreatedAt() != null) {
                this.paidAt = DateUtil.format(payment.getCreatedAt());
            }
        }

        public ListDTO(Payment payment) {
            this(payment, payment.isPaid());
        }
    }
}
