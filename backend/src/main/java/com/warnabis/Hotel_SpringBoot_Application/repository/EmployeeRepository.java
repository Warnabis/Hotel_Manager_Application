package com.warnabis.Hotel_SpringBoot_Application.repository;

import com.warnabis.Hotel_SpringBoot_Application.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE " +
      "(:fullName IS NULL OR e.fullName LIKE CONCAT('%', CAST(:fullName AS string), '%')) AND " +
      "(:experience IS NULL OR e.experience = :experience) AND " +
      "(:schedule IS NULL OR e.schedule = :schedule) AND " +
      "(:phoneNumber IS NULL OR e.phoneNumber LIKE CONCAT('%', CAST(:phoneNumber AS string), '%'))")
    Page<Employee> findWithFilters(@Param("fullName") String fullName,
                                   @Param("experience") String experience,
                                   @Param("schedule") String schedule,
                                   @Param("phoneNumber") String phoneNumber,
                                   Pageable pageable);
}