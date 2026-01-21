package org.example.blog.reply;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception403;
import org.example.blog._core.errors.exception.Exception404;
import org.example.blog.board.Board;
import org.example.blog.board.BoardRepository;
import org.example.blog.user.User;
import org.example.blog.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reply save(ReplyRequest.SaveDTO saveDTO, Long sessionUserId) {
        Board boardEntity = boardRepository.findById(saveDTO.getBoardId())
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다."));

        User userEntity = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        Reply reply = saveDTO.toEntity(boardEntity, userEntity);
        return replyRepository.save(reply);
    }

    public List<ReplyResponse.ListDTO> replyList(Long boardId, Long sessionUserId) {
        List<Reply> replyListEntity = replyRepository.findByBoardIdWithUser(boardId);
        return replyListEntity.stream()
                .map(reply -> new ReplyResponse.ListDTO(
                        reply, sessionUserId))
                .toList();
    }

    @Transactional
    public void delete(Long replyId, Long userId) {
        Reply replyEntity = replyRepository.findByIdWithUser(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다."));
        if(!replyEntity.isOwner(userId)) {
            throw new Exception403("댓글 삭제 권한이 없습니다.");
        }
        replyRepository.delete(replyEntity);
    }

}
