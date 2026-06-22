package org.example.models;

import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class Service extends EntityWithId {
    private String title;
    private String description;
    private BigDecimal price;
    private String duration;

    public Service(String title, String description, BigDecimal price, String duration) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }
}
