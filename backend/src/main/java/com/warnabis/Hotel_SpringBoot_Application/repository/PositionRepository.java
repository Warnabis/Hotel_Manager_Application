package com.warnabis.Hotel_SpringBoot_Application.repository;

import com.warnabis.Hotel_SpringBoot_Application.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PositionRepository extends JpaRepository<Position, Long> {

    @Query("SELECT p FROM Position p WHERE " +
      "(:title IS NULL OR p.title LIKE CONCAT('%', CAST(:title AS string), '%')) AND " +
      "(:salary IS NULL OR p.salary = :salary) AND " +
      "(:description IS NULL OR p.responsibilities LIKE CONCAT('%', CAST(:description AS string), '%'))")
    Page<Position> findWithFilters(@Param("title") String title,
                                   @Param("salary") Double salary,
                                   @Param("description") String description,
                                   Pageable pageable);
}