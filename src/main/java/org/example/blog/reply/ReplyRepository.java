package org.example.blog.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("""
        SELECT r
        FROM Reply r
        JOIN FETCH r.board
        JOIN FETCH r.user
        WHERE r.board.id = :boardId
        ORDER BY r.createdAt ASC
""")
    List<Reply> findByBoardIdWithUser(@Param("boardId") Long boardId);

    @Query("""
        SELECT r
        FROM Reply r
        JOIN FETCH r.user
        JOIN FETCH r.board
        WHERE r.id = :id
        ORDER BY r.createdAt ASC
""")
    Optional<Reply> findByIdWithUser(@Param("id") Long id);

    void deleteByBoardId(Long boardId);
}
