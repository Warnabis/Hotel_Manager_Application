package com.warnabis.hotel_springboot_application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "experience", nullable = false)
    private String experience;

    @Column(name = "schedule", nullable = false)
    private String schedule;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @ManyToMany
    @JoinTable(
      name = "employee_position",
      joinColumns = @JoinColumn(name = "employee_id"),
      inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private List<Position> positions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
      name = "room_employee",
      joinColumns = @JoinColumn(name = "employee_id"),
      inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private List<Room> rooms = new ArrayList<>();

    @ManyToMany
    @JoinTable(
      name = "service_employee",
      joinColumns = @JoinColumn(name = "employee_id"),
      inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();
}
