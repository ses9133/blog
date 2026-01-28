package org.example.blog.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("""
        SELECT p FROM Purchase p
        JOIN FETCH p.board b
        JOIN FETCH b.user
            WHERE p.user.id = :userId
            ORDER BY p.createdAt
""")
    List<Purchase> findAllByUserIdWithBoard(@Param("userId") Long userId);

    @Query("SELECT p FROM Purchase p WHERE p.user.id = :userId AND p.board.id = :boardId")
    Optional<Purchase> findByUserIdAndBoardId(@Param("userId") Long userId, @Param("boardId") Long boardId);

    default boolean existsByUserIdAndBoardId(Long userId, Long boardId) {
        return findByUserIdAndBoardId(userId, boardId).isPresent();
    }
}
