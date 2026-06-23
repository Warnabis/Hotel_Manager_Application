package com.warnabis.Hotel_SpringBoot_Application.repository;

import com.warnabis.Hotel_SpringBoot_Application.model.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    @Query("SELECT g FROM Guest g WHERE " +
      "(:fullName IS NULL OR g.fullName LIKE CONCAT('%', CAST(:fullName AS string), '%')) AND " +
      "(:phoneNumber IS NULL OR g.phoneNumber LIKE CONCAT('%', CAST(:phoneNumber AS string), '%')) AND " +
      "(:email IS NULL OR g.email LIKE CONCAT('%', CAST(:email AS string), '%')) AND " +
      "(:status IS NULL OR g.status = :status)")
    Page<Guest> findWithFilters(@Param("fullName") String fullName,
                                @Param("phoneNumber") String phoneNumber,
                                @Param("email") String email,
                                @Param("status") String status,
                                Pageable pageable);
}