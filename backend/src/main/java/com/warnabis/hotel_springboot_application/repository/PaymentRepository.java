package com.warnabis.hotel_springboot_application.repository;

import com.warnabis.hotel_springboot_application.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE " +
      "(:status IS NULL OR CAST(p.status AS string) = :status) AND " +
      "(:amount IS NULL OR p.amount = :amount) AND " +
      "(:paymentDate IS NULL OR p.paymentDate = :paymentDate) AND " +
      "(:paymentMethod IS NULL OR CAST(p.paymentMethod AS string) = :paymentMethod)")
    Page<Payment> findWithFilters(@Param("status") String status,
                                  @Param("amount") Double amount,
                                  @Param("paymentDate") LocalDate paymentDate,
                                  @Param("paymentMethod") String paymentMethod,
                                  Pageable pageable);
}