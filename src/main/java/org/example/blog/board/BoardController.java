package org.example.blog.board;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.blog._core.constants.SessionConstants;
import org.example.blog._core.response.ApiResponse;
import org.example.blog.purchase.PurchaseService;
import org.example.blog.reply.ReplyResponse;
import org.example.blog.reply.ReplyService;
import org.example.blog.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final PurchaseService purchaseService;

    @GetMapping("/boards/new")
    public String saveForm() {
        return "board/save-form";
    }

    @PostMapping("/boards")
    public String saveProc(BoardRequest.SaveDTO saveDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        saveDTO.validate();
        boardService.saveProc(saveDTO, sessionUser);
        return "redirect:/";
    }

    @GetMapping("/boards/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        Long sessionUserId = sessionUser != null ? sessionUser.getId() : null;

        BoardResponse.DetailDTO board = boardService.detail(id, sessionUserId);

        boolean isOwner = false;
        if(sessionUser != null && board.getUserId() !=null) {
            isOwner = board.getUserId().equals(sessionUser.getId());
        }

        List<ReplyResponse.ListDTO> replyList = replyService.replyList(id, sessionUserId);

        model.addAttribute("replyList", replyList);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("board", board);

        return "board/detail";
    }

    @GetMapping({"/boards", "/"})
    public String boardList(Model model,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "3") int size,
                            @RequestParam(required = false) String keyword) {
        int pageIndex = Math.max(0, page - 1);

        BoardResponse.PageDTO boardPage = boardService.boardList(pageIndex, size, keyword);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardPage", boardPage);

        return "board/list";
    }


    @GetMapping("/boards/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);

        BoardResponse.UpdateFormDTO dto = boardService.updateForm(id, sessionUser.getId());
        model.addAttribute("board", dto);

        return "board/update-form";
    }

    @PutMapping("/api/v1/boards/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse<BoardResponse.DetailDTO>> updateProc(@PathVariable Long id, @RequestBody BoardRequest.UpdateDTO updateDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);

        updateDTO.validate();
        BoardResponse.DetailDTO data = boardService.updateProc(updateDTO, id, sessionUser.getId());

        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @DeleteMapping("/api/v1/boards/{id}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, HttpSession session) {
        User sessionUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        boardService.delete(id, sessionUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("게시글이 삭제되었습니다."));
    }

    // TODO - 결제 관련 리팩토링 (-)
    @PostMapping("/board/{boardId}/purchase")
    public String purchase(@PathVariable Long boardId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        purchaseService.구매하기(sessionUser.getId(), boardId);
        return "redirect:/board/" + boardId;

    }

}
