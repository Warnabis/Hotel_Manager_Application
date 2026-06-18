package org.example.models;

import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private int id;
    private String title;
    private BigDecimal salary;
    private String responsibilities;

    public Position(String title, BigDecimal salary, String responsibilities) {
        this.title = title;
        this.salary = salary;
        this.responsibilities = responsibilities;
    }
}
