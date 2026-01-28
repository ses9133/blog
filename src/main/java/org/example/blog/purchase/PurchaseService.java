package org.example.blog.purchase;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception400;
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
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    private static final Integer PREMIUM_BOARD_PRICE = 500;

    @Transactional
    public void purchaseBoard(Long userId, Long boardId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("해당 게시글이 존재하지 않습니다."));

        if(!board.getPremium()) {
            throw new Exception400("유료 게시글이 아닙니다.");
        }

        if(board.isOwner(userId)) {
            throw new Exception400("본인의 게시글은 구매할 수 없습니다.");
        }

        if(purchaseRepository.existsByUserIdAndBoardId(userId, boardId)) {
            throw new Exception400("이미 구매한 게시글입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 조회할 수 없습니다."));

        user.deductPoint(PREMIUM_BOARD_PRICE);

        Purchase purchase = Purchase.builder()
                .user(user)
                .board(board)
                .price(PREMIUM_BOARD_PRICE)
                .build();
        purchaseRepository.save(purchase);

        userRepository.save(user);
    }

    public boolean confirmIsPurchased(Long userId, Long boardId) {
        if(userId == null) {
            return false;
        }
        return purchaseRepository.existsByUserIdAndBoardId(userId, boardId);
    }

    public List<PurchaseResponse.ListDTO> purchaseList(Long userId) {
        List<Purchase> purchaseList = purchaseRepository.findAllByUserIdWithBoard(userId);

        return purchaseList.stream()
                .map(PurchaseResponse.ListDTO::new).toList();
    }

}
