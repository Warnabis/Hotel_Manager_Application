package org.example.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingPayment {

    private int id;
    private int bookingId;
    private int paymentId;

    public BookingPayment(int bookingId, int paymentId) {
        this.bookingId = bookingId;
        this.paymentId = paymentId;
    }
}
