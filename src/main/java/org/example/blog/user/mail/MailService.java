package org.example.blog.user.mail;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception500;
import org.example.blog._core.utils.MailUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final HttpSession session;

    public void sendVerificationEmail(String email) {
        // 1. 랜덤코드 생성
        String code = MailUtil.generateRandomCode();

        // 2. 이메일 전송 내용 설정
        MimeMessage message = javaMailSender.createMimeMessage();

        // 3. 구글 메일 서버로 전송
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[MyBlog] 회원가입 이메일 전송");
            helper.setText("<h3>인증번호는 [" + code + "] 입니다. </h3>", true);

            javaMailSender.send(message);

            session.setAttribute("code_" + email, code);
        } catch (Exception e) {
            throw new Exception500("이메일 인증번호 전송 중 오류가 발생했습니다.");
        }
    }

    public boolean verifyEmailCode(String email, String code) {
        String savedCode = (String) session.getAttribute("code_" + email);

        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("code_" + email);
            return true;
        }
        return false;
    }
}