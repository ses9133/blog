package org.example.blog.board;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception403;
import org.example.blog._core.errors.exception.Exception404;
import org.example.blog.purchase.PurchaseService;
import org.example.blog.reply.ReplyRepository;
import org.example.blog.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final PurchaseService purchaseService;

    @Transactional
    public void saveProc(BoardRequest.SaveDTO saveDTO, User sessionUser) {
        Board board = saveDTO.toEntity(sessionUser);
        boardRepository.save(board);
    }

    public BoardResponse.DetailDTO detail(Long boardId, Long userId) {
        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다."));

        boolean isPurchased = false;
        if (userId != null) {
            isPurchased = purchaseService.confirmIsPurchased(userId, boardId);
        }
        return new BoardResponse.DetailDTO(board, isPurchased);
    }


    public BoardResponse.PageDTO boardList(int page, int size, String keyword) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<Board> boardPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            boardPage = boardRepository.findByTitleContainingOrContentContaining(keyword.trim(), pageable);
        } else {
            boardPage = boardRepository.findAllWithUserByOrderByCreatedAtDesc(pageable);
        }
        return new BoardResponse.PageDTO(boardPage);
    }


    public BoardResponse.UpdateFormDTO updateForm(Long boardId, Long sessionUserId) {
        Board boardEntity = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다."));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }
        return new BoardResponse.UpdateFormDTO(boardEntity);
    }

    @Transactional
    public BoardResponse.DetailDTO updateProc(BoardRequest.UpdateDTO updateDTO, Long boardId, Long sessionUserId) {
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다."));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }
        boardEntity.update(updateDTO);
        boolean isPurchased = purchaseService.confirmIsPurchased(sessionUserId, boardId);

        return new BoardResponse.DetailDTO(boardEntity, isPurchased);
    }

    @Transactional
    public void delete(Long boardId, Long sessionUserId) {
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("해당 게시글을 찾을 수 없습니다."));

        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }
        replyRepository.deleteByBoardId(boardId);
        boardRepository.deleteById(boardId);
    }
}
