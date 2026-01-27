package org.example.blog.refund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    @Query("""
        SELECT r FROM Refund r
        JOIN FETCH r.payment p
        JOIN FETCH p.user u
            WHERE r.user.id = :userId
        ORDER BY r.createdAt DESC
""")
    List<Refund> findAllByUserId(@Param("userId") Long userId);

    Optional<Refund> findByPaymentId(Long paymentId);

    @Query("""
        SELECT r FROM Refund r
        JOIN FETCH r.user u
        JOIN FETCH r.payment p
        ORDER BY r.createdAt DESC
""")
    List<Refund> findAllWithUserAndPayment();

    @Query("""
        SELECT r FROM Refund r
        JOIN FETCH r.user u
        JOIN FETCH r.payment p
            WHERE r.id = :refundId
""")
    Optional<Refund> findByIdWithUserAndPayment(@Param("refundId") Long refundId);
}
