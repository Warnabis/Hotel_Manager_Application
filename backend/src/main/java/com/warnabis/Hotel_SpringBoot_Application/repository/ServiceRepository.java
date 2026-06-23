package com.warnabis.Hotel_SpringBoot_Application.repository;

import com.warnabis.Hotel_SpringBoot_Application.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    @Query("SELECT s FROM Service s WHERE " +
      "(:title IS NULL OR s.title LIKE CONCAT('%', CAST(:title AS string), '%')) AND " +
      "(:price IS NULL OR s.price = :price) AND " +
      "(:duration IS NULL OR s.duration = :duration)")
    Page<Service> findWithFilters(@Param("title") String title,
                                  @Param("price") Double price,
                                  @Param("duration") String duration,
                                  Pageable pageable);
}