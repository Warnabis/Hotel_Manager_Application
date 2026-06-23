package com.warnabis.Hotel_SpringBoot_Application.repository;

import com.warnabis.Hotel_SpringBoot_Application.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE " +
      "(:floor IS NULL OR r.floor = :floor) AND " +
      "(:type IS NULL OR CAST(r.type AS string) = :type) AND " +
      "(:status IS NULL OR CAST(r.status AS string) = :status) AND " +
      "(:price IS NULL OR r.price = :price)")
    Page<Room> findWithFilters(@Param("floor") Integer floor,
                               @Param("type") String type,
                               @Param("status") String status,
                               @Param("price") Double price,
                               Pageable pageable);
}