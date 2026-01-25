package org.example.blog.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    // TODO - 포인트 관련 리팩토링 필요
    // api/point/charge
    @PostMapping("/api/point/charge")
    public ResponseEntity<?> chargePoint(@RequestBody UserRequest.PointChargeDTO reqDTO, HttpSession session) {
        reqDTO.validate();

        User sessionUser = (User) session.getAttribute("sessionUser");
        if(sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        // 포인트 충전 처리
        User updatedUser = userService.포인트충전(sessionUser.getId(), reqDTO.getAmount());

        // 세션에 업데이트된 사용자 정보 갱신(포인트)
        session.setAttribute("sessionUser", updatedUser);
        return ResponseEntity.ok()
                .body(Map.of("message", "포인트가 충전되었슶니다.",
                        "amount", reqDTO.getAmount(),
                        "currentPoint", updatedUser.getPoint()));
    }
}
