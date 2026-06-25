package com.warnabis.hotel_springboot_application.repository;

import com.warnabis.hotel_springboot_application.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE " +
      "(:id IS NULL OR b.id = :id) AND " +
      "(:price IS NULL OR b.price = :price) AND " +
      "(:status IS NULL OR b.status = :status) AND " +
      "(:checkInDate IS NULL OR CAST(b.checkInDate AS string) = CAST(:checkInDate AS string)) AND " +
      "(:duration IS NULL OR b.duration = :duration) AND " +
      "(:guestId IS NULL OR b.guest.id = :guestId)")
    Page<Booking> findWithFilters(@Param("id") Long id,
                                  @Param("price") Double price,
                                  @Param("status") String status,
                                  @Param("checkInDate") LocalDate checkInDate,
                                  @Param("duration") String duration,
                                  @Param("guestId") Integer guestId,
                                  Pageable pageable);
}
