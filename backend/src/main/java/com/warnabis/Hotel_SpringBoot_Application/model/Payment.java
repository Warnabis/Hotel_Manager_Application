package com.warnabis.Hotel_SpringBoot_Application.model;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.persistence.*;

@Entity
@Table(name = "payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "method", nullable = false)
    private String paymentMethod;

    @ManyToMany
    @JoinTable(
      name = "booking_payment",
      joinColumns = @JoinColumn(name = "payment_id"),
      inverseJoinColumns = @JoinColumn(name = "booking_id")
    )
    private List<Booking> bookings = new ArrayList<>();

    @ManyToMany
    @JoinTable(
      name = "service_payment",
      joinColumns = @JoinColumn(name = "payment_id"),
      inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Guest guest;
}
