package org.example.blog.payment;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception400;
import org.example.blog._core.errors.exception.Exception404;
import org.example.blog._core.errors.exception.Exception500;
import org.example.blog.refund.Refund;
import org.example.blog.refund.RefundRepository;
import org.example.blog.user.User;
import org.example.blog.user.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
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
    private final RefundRepository refundRepository;

    @Value("${portone.imp-secret}")
    private String impSecret;

    @Transactional
    public PaymentResponse.PrepareDTO preparePayment(Long userId, Integer amount) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception400("사용자를 찾을 수 없습니다."));

        String paymentId = generatePaymentId();
        while (paymentRepository.existsByPaymentId(paymentId)) {
            paymentId = generatePaymentId();
        }

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .user(userEntity)
                .amount(amount)
                .paymentStatus(PaymentStatus.READY)
                .build();

        paymentRepository.save(payment);

        return new PaymentResponse.PrepareDTO(payment.getPaymentId(), amount);
    }

    private String generatePaymentId() {
        return "B" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional
    public PaymentResponse.VerifyDTO verifyPaymentAndCharge(Long userId, String paymentId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);

        // 유효한 결제 요청인지 확인
        if(paymentOpt.isEmpty()) {
            throw new Exception400("결제 요청을 찾을 수 없습니다.");
        }

        Payment payment = paymentOpt.get();

        // 중복 결제 방지
        if(payment.isPaid()) {
            throw new Exception400("이미 결제된 내역입니다.");
        }

        // 결제 상태 확인
        if(payment.isCancelled()) {
            throw new Exception400("취소된 결제내역은 재결제 불가능합니다.");
        }

        PaymentResponse.PortOneV2Response paymentData = getPortOnePayment(paymentId);
        Integer responseAmount = paymentData.getAmount().getTotal();

        // 결제 금액 위변조 방지
        if(!responseAmount.equals(payment.getAmount())) {
            throw new Exception400("결제 요청 금액이 불일치합니다.");
        }

        userEntity.chargePoint(paymentData.getAmount().getTotal());
        payment.updateStatus(PaymentStatus.PAID);

        return new PaymentResponse.VerifyDTO(paymentData.getAmount().getTotal(), userEntity.getPoint());
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

    public List<PaymentResponse.ListDTO> paymentList(Long userId) {
        List<Payment> paymentList = paymentRepository.findByUserId(userId);

        return paymentList.stream()
                .map(payment -> {
                    Optional<Refund> refundOpt = refundRepository.findByPaymentId(payment.getId());

                    boolean isRefundable = false;

                    if(payment.isPaid()) {
                        if(refundOpt.isPresent() && refundOpt.get().isPending()) {
                            isRefundable = true;
                        } else if(refundOpt.isEmpty()) {
                            isRefundable = true;
                        }
                    }
                    return new PaymentResponse.ListDTO(payment, isRefundable);
                }).toList();
    }
}