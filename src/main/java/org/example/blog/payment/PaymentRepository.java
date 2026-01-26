package org.example.blog.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.paymentId = :paymentId")
    boolean existsByPaymentId(@Param("paymentId") String paymentId);

    List<Payment> findByUserId(Long userId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.user WHERE p.id = :paymentId")
    Payment findByIdWithUser(@Param("paymentId") Long paymentId);
}