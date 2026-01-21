package org.example.blog.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog._core.response.ApiResponse;
import org.example.blog.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping("/replies")
    public String saveProc(ReplyRequest.SaveDTO saveDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        saveDTO.validate();
        replyService.save(saveDTO, sessionUser.getId());
        return "redirect:/boards/" + saveDTO.getBoardId();
    }

    @GetMapping("/board/{boardId}/replies")
    public List<ReplyResponse.ListDTO> replyList(@PathVariable Long boardId, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        return replyService.replyList(boardId, sessionUser.getId());
    }

    @DeleteMapping("/api/v1/replies/{replyId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long replyId, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        replyService.delete(replyId, sessionUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("댓글 삭제에 성공했습니다."));
    }
}
