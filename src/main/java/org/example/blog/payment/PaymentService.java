package org.example.blog.payment;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception400;
import org.example.blog._core.errors.exception.Exception404;
import org.example.blog._core.errors.exception.Exception500;
import org.example.blog.refund.RefundRequest;
import org.example.blog.refund.RefundRequestRepository;
import org.example.blog.user.User;
import org.example.blog.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final RefundRequestRepository refundRequestRepository;

    @Value("${portone.imp-secret}")
    private String impSecret;

    @Transactional
    public PaymentResponse.PrepareDTO preparePayment(Long userId, Integer amount) {
        if(!userRepository.existsById(userId)) {
            throw new Exception400("사용자를 찾을 수 없습니다.");
        }

        String paymentId = generatePaymentId();
        while (paymentRepository.existsByPaymentId(paymentId)) {
            paymentId = generatePaymentId();
        }

        return new PaymentResponse.PrepareDTO(paymentId, amount);
    }

    private String generatePaymentId() {
        return "B" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional
    public PaymentResponse.VerifyDTO verifyPaymentAndCharge(Long userId, String paymentId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        // 중복 결제 방지
        paymentRepository.findByPaymentId(paymentId).ifPresent(payment -> {
            if(payment.isPaid()) {
                throw new Exception400("이미 결제된 내역입니다.");
            }
        });

        PaymentResponse.PortOneV2Response paymentData = getPortOnePayment(paymentId);

        // 결제 금액 위변조 방지


        user.chargePoint(paymentData.getAmount().getTotal());

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .user(user)
                .amount(paymentData.getAmount().getTotal())
                .paymentStatus(PaymentStatus.PAID)
                .build();

        paymentRepository.save(payment);

        return new PaymentResponse.VerifyDTO(paymentData.getAmount().getTotal(), user.getPoint());
    }

    // 포트원 결제 조회
    private PaymentResponse.PortOneV2Response getPortOnePayment(String paymentId) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "PortOne " + impSecret);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<PaymentResponse.PortOneV2Response> response = restTemplate.exchange(
                    "https://api.portone.io/payments/" + paymentId,
                    HttpMethod.GET,
                    request,
                    PaymentResponse.PortOneV2Response.class);

            PaymentResponse.PortOneV2Response data = response.getBody();

            if (data == null) {
                throw new Exception400("결제 정보를 찾을 수 없습니다.");
            }

            // ** 데이터 무결성 검증 **
            // 결제 상태 확인
            if (!"PAID".equals(data.getStatus())) {
                throw new Exception400("결제가 완료되지 않았습니다.");
            }

            // 결제 번호 일치 확인
            if (!paymentId.equals(data.getId())) {
                throw new Exception400("주문 번호가 일치하지 않습니다.");
            }

            return data;
        }  catch (Exception e) {
            throw new Exception500("결제 정보 조회 중 오류 발생: " + e.getMessage());
        }
    }

    // TODO - 환불 요청 관련 리팩토링 필요
    public List<PaymentResponse.ListDTO> paymentList(Long userId) {
        List<Payment> paymentList = paymentRepository.findByUserId(userId);

        return paymentList.stream()
                .map(payment -> {
                    // 환불 요청 조회
                    // 결제 PK 값으로 환불 테이블에 이력이 있는지 없는지 조회
                    Optional<RefundRequest> refundRequestOpt = refundRequestRepository.findByPaymentId(payment.getId());

                    // 환불 요청이 있는 경우 상태 확인
                    // 요청이 있으면 화면에 '환불 요청' 버튼 보이면 안됨
                    boolean hasRefundRequest = refundRequestOpt.isPresent();
                    boolean isRefundable = false;

                    if("paid".equals(payment.getPaymentStatus().toString())) {
                        // 결제 완료인 상태
                        if(!hasRefundRequest) {
                            // 환불 요청이 없는 상태 -> 환불 가능한 상태임
                            isRefundable = true;
                        } else {
                            // 환불 요청 대기 상태 --> 원래 isRefundable = false 임 (즉 화면ㅇ ㅔ버튼 안보임)
                            RefundRequest refundRequest = refundRequestOpt.get();
                            //관리자가 환불 거절했지만 다시 요청할수있게 한다면
                            if(refundRequest.isRejected()) {
                                isRefundable = true;
                            } else {
                                // 대기중 / 환불 완료 상태
                                isRefundable = false;
                            }
                        }
                    } else {
                        // 환불 완료 상태 (돈 내어줌)
                        isRefundable = false;
                    }
                    return new PaymentResponse.ListDTO(payment, isRefundable);
                }).toList();
    }
}