package org.example.models;

import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class Room extends EntityWithId {
    private int floor;
    private String type;
    private String status;
    private BigDecimal price;

    public Room(int floor, String type, String status, BigDecimal price) {
        this.floor = floor;
        this.type = type;
        this.status = status;
        this.price = price;
    }
}
