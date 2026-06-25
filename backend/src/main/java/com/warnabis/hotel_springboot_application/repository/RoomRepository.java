package com.warnabis.hotel_springboot_application.repository;

import com.warnabis.hotel_springboot_application.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE " +
      "(:floor IS NULL OR r.floor = :floor) AND " +
      "(:type IS NULL OR LOWER(r.type) = LOWER(CAST(:type AS string))) AND " +
      "(:status IS NULL OR LOWER(r.status) = LOWER(CAST(:status AS string))) AND " +
      "(:price IS NULL OR r.price = :price)")
    Page<Room> findWithFilters(@Param("floor") Integer floor,
                               @Param("type") String type,
                               @Param("status") String status,
                               @Param("price") Double price,
                               Pageable pageable);
}