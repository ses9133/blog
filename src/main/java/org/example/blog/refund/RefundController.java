package org.example.blog.refund;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog.payment.Payment;
import org.example.blog.payment.PaymentResponse;
import org.example.blog.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/refunds")
public class RefundController {
    private final RefundService refundService;

    @GetMapping("/{paymentId}")
    public String refundRequestForm(@PathVariable Long paymentId, Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        Payment payment = refundService.refundRequestForm(paymentId, sessionUser.getId());
        PaymentResponse.ListDTO paymentDTO = new PaymentResponse.ListDTO(payment);
        model.addAttribute("payment", paymentDTO);
        return "refund/request-form";
    }

    @PostMapping
    public String refundRequest(RefundRequest.DTO reqDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        reqDTO.validate();
        refundService.refundRequest(sessionUser.getId(), reqDTO);
        return "redirect:/refunds";
    }

    // 내 환불 요청 내역 조회
    @GetMapping
    public String refundList(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        List<RefundResponse.ListDTO> refundList = refundService.refundList(sessionUser.getId());
        model.addAttribute("refundList", refundList);
        return "refund/list";
    }
}
