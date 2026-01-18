package org.example.blog.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query(
            value = """
        SELECT DISTINCT b FROM Board b
        JOIN FETCH b.user
        ORDER BY b.createdAt DESC
    """,
            countQuery = """
        SELECT COUNT(b) FROM Board b
    """
    )
    Page<Board> findAllWithUserByOrderByCreatedAtDesc(Pageable pageable);

    @Query(
            value = """
        SELECT DISTINCT b FROM Board b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY b.createdAt DESC
    """,
            countQuery = """
        SELECT COUNT(DISTINCT b) FROM Board b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """
    )
    Page<Board> findByTitleContainingOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT b FROM Board b
            JOIN FETCH b.user
            WHERE b.id = :id
""")
    Optional<Board> findByIdWithUser(@Param("id") Long userId);
}
