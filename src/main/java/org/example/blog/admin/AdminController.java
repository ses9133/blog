package org.example.blog.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog._core.errors.exception.Exception400;
import org.example.blog.refund.RefundResponse;
import org.example.blog.refund.RefundService;
import org.example.blog.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final RefundService refundService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("user", sessionUser);
        return "admin/dashboard";
    }

    @GetMapping("/refunds")
    public String getAllRefundsForAdmin(Model model) {
        List<RefundResponse.AdminListDTO> refundList = refundService.getAllRefundsForAdmin();
        model.addAttribute("refundList", refundList);
        return "admin/admin-refund-list";
    }

    @PostMapping("/refunds/{id}/reject")
    public String rejectRefund(@PathVariable Long id, @RequestParam(name = "rejectReason") String rejectReason) {
        if(rejectReason == null || rejectReason.trim().isEmpty()) {
            throw new Exception400("거절 사유를 입력해주세요");
        }
        refundService.rejectRefund(id, rejectReason);
        return "redirect:/admin/refunds";
    }

    @PostMapping("/refunds/{id}/approve")
    public String approveRefund(@PathVariable Long id) {
        refundService.approveRefund(id);
        return "redirect:/admin/refunds";
    }
}

