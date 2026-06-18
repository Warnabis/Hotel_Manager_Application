package org.example.models;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private int id;
    private String status;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private int guestId;

    public Payment(String status, BigDecimal amount, LocalDate paymentDate, String paymentMethod, int guestId) {
        this.status = status;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.guestId = guestId;
    }
}
