package org.example.blog.user.mail;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.response.ApiResponse;
import org.example.blog.user.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@RequestBody UserRequest.EmailVerifyDTO reqDTO) {
        reqDTO.validate();

        mailService.sendVerificationEmail(reqDTO.getEmail());

        return ResponseEntity.ok().body(ApiResponse.ok("인증번호 발송에 성공했습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmailCode(@RequestBody UserRequest.EmailCheckDTO reqDTO) {
        reqDTO.validate();

        boolean isVerified = mailService.verifyEmailCode(reqDTO.getEmail(), reqDTO.getCode());

        if(isVerified) {
            return ResponseEntity.ok().body(ApiResponse.ok("이메일 인증에 성공했습니다."));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail("이메일 인증에 실패했습니다."));
        }
    }
}
