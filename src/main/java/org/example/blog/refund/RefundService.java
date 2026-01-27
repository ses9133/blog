package org.example.blog.refund;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception400;
import org.example.blog._core.errors.exception.Exception403;
import org.example.blog._core.errors.exception.Exception404;
import org.example.blog._core.errors.exception.Exception500;
import org.example.blog.payment.Payment;
import org.example.blog.payment.PaymentRepository;
import org.example.blog.payment.PaymentStatus;
import org.example.blog.user.User;
import org.example.blog.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundService {

    @Value("${portone.imp-secret}")
    private String impSecret;

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final UserRepository userRepository;

    // 0 단계: 환불 요청화면 진입시 검증
    public Payment refundRequestForm(Long paymentId, Long userId) {

        Payment payment = paymentRepository.findByIdWithUser(paymentId);

        // 본인 확인
        if(!payment.getUser().getId().equals(userId)) {
            throw new Exception403("본인 결제 내역만 환불 요청 가능합니다.");
        }

        // 결제 완료 상태 인지 확인
        if(!payment.isPaid()) {
            throw new Exception400("결제 완료된 상태만 환불 요청 가능합니다.");
        }

        // 이미 환불 요청 한 상태인지 확인
        if(refundRepository.findByPaymentId(paymentId).isPresent()) {
            throw new Exception400("이미 환불 요청이 진행중입니다.");
        }

        return payment;
    }

    @Transactional
    public void refundRequest(Long userId, RefundRequest.DTO reqDTO) {
        Payment payment = refundRequestForm(reqDTO.getPaymentId(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        Refund refund = Refund.builder()
                .user(user)
                .payment(payment)
                .reason(reqDTO.getReason())
                .build();
        refundRepository.save(refund);
    }

    public List<RefundResponse.ListDTO> refundList(Long userId) {
        List<Refund> refundList = refundRepository.findAllByUserId(userId);
        return refundList.stream()
                .map(RefundResponse.ListDTO::new)
                .toList();
    }

    public List<RefundResponse.AdminListDTO> getAllRefundsForAdmin() {
        List<Refund> refundList = refundRepository.findAllWithUserAndPayment();
        return refundList.stream()
                .map(RefundResponse.AdminListDTO::new)
                .toList();
    }

    @Transactional
    public void rejectRefund(Long refundRequestId, String rejectReason) {
        Refund refund = refundRepository.findById(refundRequestId)
                .orElseThrow(() -> new Exception404("환불 요청을 찾을 수 없습니다."));
        if(!refund.isPending()) {
            throw new Exception400( "대기 중인 환불 요청만 거절 할 수 있습니다.");
        }
        refund.reject(rejectReason);
    }

    @Transactional
    public void approveRefund(Long refundRequestId) {
        Refund refund = refundRepository.findByIdWithUserAndPayment(refundRequestId)
                .orElseThrow(() -> new Exception404("환불 요청을 찾을 수 없습니다."));

        if(!refund.isPending()) {
            throw new Exception400("대기 중인 환불 요청만 승인할 수 있습니다.");
        }

        Payment payment = refund.getPayment();
        User user = refund.getUser();
        Integer refundAmount = payment.getAmount();

        if(user.getPoint() < refundAmount) {
            throw new Exception400("포인트를 이미 사용하여 환불이 불가능합니다.");
        }

        cancelPortOnePayment(payment.getPaymentId());

        refund.setStatus(RefundStatus.APPROVED);
        payment.setPaymentStatus(PaymentStatus.CANCELLED);

        user.deductPoint(refundAmount);
    }

    private void cancelPortOnePayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new Exception404("결제 내역이 존재하지 않습니다."));

        Refund refund = refundRepository.findByPaymentId(payment.getId())
                .orElseThrow(() -> new Exception404("환불 요청 내역이 존재하지 않습니다."));
        String reason = refund.getReason();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "PortOne " + impSecret);

        Map<String, Object> body = new HashMap<>();
        body.put("reason", reason);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<RefundResponse.PortOneDTO> response = restTemplate.exchange(
                    "https://api.portone.io/payments/{paymentId}/cancel",
                    HttpMethod.POST,
                    request,
                    RefundResponse.PortOneDTO.class,
                    paymentId
            );

            RefundResponse.PortOneDTO responseBody = response.getBody();

            if(responseBody == null) {
                throw new Exception500("포트원 결제취소 응답이 비어있습니다.");
            }

            RefundResponse.PortOneDTO.Cancellation cancellation = responseBody.getCancellation();
            if(!payment.getAmount().equals(cancellation.getTotalAmount())) {
                throw new Exception400("결제 금액과 환불 요청 금액이 불일치합니다.");
            }

            if(!cancellation.getStatus().equals("SUCCEEDED")) {
                throw new Exception500("환불에 실패했습니다.");
            }

        } catch (Exception e) {
                throw new Exception500("포트원 결제 취소중 오류 발생");
        }
    }


}
