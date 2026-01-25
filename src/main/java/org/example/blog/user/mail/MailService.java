package org.example.blog.user.mail;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception500;
import org.example.blog._core.utils.MailUtils;
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
        String code = MailUtils.generateRandomCode();

        // 2. 이메일 전송 내용 설정
        MimeMessage message = javaMailSender.createMimeMessage();

        // 3. 구글 메일 서버로 전송(외부 서버로 통신 요청)
        try {
            // 3.1 도우미 객체를 사용
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email); // 수신 이메일 설정
            helper.setSubject("[MyBlog] 회원가입 이메일 전송"); // 메일 제목 설정
            helper.setText("<h3>인증번호는 [" + code + "] 입니다. </h3>", true); // 메일 내용 설정

            javaMailSender.send(message);

            // 4. 세션에 임시 코드 저장
            session.setAttribute("code_" + email, code);
            // 왜 DB가 아닌 세션에 저장하는 이유 ?
            // 로그인 전 단계인 회원가입 과정에서 임시 데이터를 가볍게 관리하기 위해 서버 세션을 활용
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

