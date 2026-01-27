package org.example.blog.refund;

import lombok.Data;
import lombok.Getter;
import org.example.blog._core.utils.DateUtil;

public class RefundResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private String paymentId;
        private Integer amount;
        private String reason;
        private String rejectReason;
        private String statusDisplay;

        private boolean isPending;
        private boolean isApproved;
        private boolean isRejected;

        public ListDTO(Refund refund) {
            this.id = refund.getId();
            this.paymentId = refund.getPayment().getPaymentId();
            this.amount = refund.getPayment().getAmount();
            this.reason = refund.getReason();
            this.rejectReason = refund.getRejectReason() == null ? "" : refund.getRejectReason();

            switch (refund.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECT -> this.statusDisplay = "거절됨";
            }
            this.isPending = refund.getStatus() == RefundStatus.PENDING;
            this.isApproved = refund.getStatus() == RefundStatus.APPROVED;
            this.isRejected = refund.getStatus() == RefundStatus.REJECT;
        }
    }

    @Data
    public static class AdminListDTO {
        private Long id;
        private String username;
        private String paymentId;
        private Integer amount;
        private String requestedAt;
        private RefundStatus status;
        private String statusDisplay;
        private String reason;
        private String rejectReason;

        public AdminListDTO(Refund refund) {
            this.id = refund.getId();
            this.username = refund.getUser().getUsername();
            this.paymentId = refund.getPayment().getPaymentId();
            this.amount = refund.getPayment().getAmount();
            if(refund.getCreatedAt() != null) {
                this.requestedAt = DateUtil.format(refund.getCreatedAt());
            }
            this.status = refund.getStatus();
            switch (refund.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECT -> this.statusDisplay = "거절됨";
            }
            this.reason = refund.getReason();
            this.rejectReason = refund.getRejectReason();
        }
    }

    @Data
    public static class PortOneDTO {
        private Cancellation cancellation;

        @Data
        public static class Cancellation {
            private String status;
            private Integer totalAmount;
        }
    }
}
