package org.example.models;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Booking extends EntityWithId {
    private BigDecimal price;
    private String status;
    private LocalDate checkInDate;
    private String duration;
    private int guestId;

    List<Guest> guests = new ArrayList<>();

    public Booking(BigDecimal price, String status, LocalDate checkInDate, String duration, int guestId) {
        this.price = price;
        this.status = status;
        this.checkInDate = checkInDate;
        this.duration = duration;
        this.guestId = guestId;
    }
}
