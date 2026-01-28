package org.example.blog.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog._core.errors.exception.Exception401;
import org.example.blog._core.response.ApiResponse;
import org.example.blog.payment.PaymentResponse;
import org.example.blog.payment.PaymentService;
import org.example.blog.purchase.PurchaseResponse;
import org.example.blog.purchase.PurchaseService;
import org.example.blog.user.kakao.KakaoService;
import org.example.blog.user.naver.NaverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final PurchaseService purchaseService;
    private final PaymentService paymentService;
    private final NaverService naverService;
    private final KakaoService kakaoService;

    @Value("${oauth.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${oauth.naver.client-id}")
    private String naverClientId;

    @GetMapping("/join")
    public String joinForm() {
        return "user/join-form";
    }

    @PostMapping("/join")
    public String joinProc(UserRequest.JoinDTO joinDTO) {
        joinDTO.validate();
        userService.joinProc(joinDTO);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login-form";
    }

    @PostMapping("/login")
    public String loginProc(UserRequest.LoginDTO loginDTO, HttpSession session) {
        loginDTO.validate();
        User sessionUser = userService.login(loginDTO);
        session.setAttribute(SessionConstants.LOGIN_USER, sessionUser);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/me")
    public String getMyPage(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        User user = userService.getMyPage(sessionUser.getId());
        model.addAttribute("user", user);
        return "user/detail";
    }

    @GetMapping("/me/edit")
    public String updateForm(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        User user = userService.updateForm(sessionUser.getId());
        model.addAttribute("user", user);

        return "user/update-form";
    }

    @PutMapping( "/api/v1/me")
    @ResponseBody
    public ResponseEntity<ApiResponse<UserResponse.DetailDTO>> updateProc(UserRequest.UpdateDTO updateDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        if(sessionUser.isLocal()) {
            updateDTO.validate();
        }
        User updatedUser = userService.updateProc(updateDTO, sessionUser.getId());
        session.setAttribute(SessionConstants.LOGIN_USER, updatedUser);

        return ResponseEntity.ok(ApiResponse.ok(new UserResponse.DetailDTO(updatedUser)));
    }

    @DeleteMapping("/api/v1/me/profile-image")
    @ResponseBody
    public ResponseEntity<ApiResponse<UserResponse.DetailDTO>> deleteProfileImage(HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        User updatedUser = userService.deleteProfileImage(sessionUser.getId());
        session.setAttribute(SessionConstants.LOGIN_USER, sessionUser);

        return ResponseEntity.ok(ApiResponse.ok(new UserResponse.DetailDTO(updatedUser)));
    }

    @GetMapping("/user/kakao")
    public String kakaoCallback(@RequestParam String code, HttpSession session) {
        try {
            User user = kakaoService.kakaoLogin(code);
            session.setAttribute(SessionConstants.LOGIN_USER, user);
            return "redirect:/";
        } catch (Exception e) {
            throw new Exception401("카카오 인증에 실패했습니다.");
        }
    }

    @GetMapping("/login/naver")
    public String naverLogin(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("NAVER_STATE", state);
        return "redirect:https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + naverClientId
                + "&state=" + state
                + "&redirect_uri=" + UriUtils.encode(naverRedirectUri, StandardCharsets.UTF_8);
    }

    @GetMapping("/user/naver")
    public String naverCallback(@RequestParam String code,
                                @RequestParam String state,
                                HttpSession session) {
        String savedState = (String) session.getAttribute("NAVER_STATE");
        if(!state.equals(savedState)) {
            throw new Exception401("잘못된 접근입니다. (state 불일치)");
        }
        User user = naverService.naverLogin(code, state);
        session.setAttribute(SessionConstants.LOGIN_USER, user);
        session.removeAttribute("NAVER_STATE");
        return "redirect:/";
    }

    @GetMapping("/me/points/charge")
    public String chargePointForm(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("user", sessionUser);
        return "user/charge-point";
    }

    @GetMapping("/me/payments")
    public String paymentList(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        List<PaymentResponse.ListDTO> paymentList = paymentService.paymentList(sessionUser.getId());
        model.addAttribute("paymentList", paymentList);
        return "user/payment-list";
    }

    @GetMapping("/me/purchases")
    public String purchaseList(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        List<PurchaseResponse.ListDTO> purchaseList = purchaseService.purchaseList(sessionUser.getId());

        model.addAttribute("purchaseList", purchaseList);
        return "user/purchase-list";
    }

}
