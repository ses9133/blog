package org.example.blog.payment;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog._core.response.ApiResponse;
import org.example.blog.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public ResponseEntity<ApiResponse<?>> preparePayment(@RequestBody PaymentRequest.PrepareDTO reqDTO, HttpSession session) {
        reqDTO.validate();
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        PaymentResponse.PrepareDTO prepareDTO = paymentService.preparePayment(sessionUser.getId(), reqDTO.getAmount());

        return ResponseEntity.ok()
                .body(ApiResponse.ok(prepareDTO));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyPayment(@RequestBody PaymentRequest.verifyDTO reqDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        reqDTO.validate();
        PaymentResponse.VerifyDTO verifyDTO = paymentService.verifyPaymentAndCharge(
                sessionUser.getId(),
                reqDTO.getPaymentId());

        sessionUser.setPoint(verifyDTO.getCurrentPoint());
        session.setAttribute(SessionConstants.LOGIN_USER, sessionUser);

        return ResponseEntity.ok()
                .body(ApiResponse.ok(verifyDTO));
    }
}
