package org.example.models;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private int id;
    private String fullName;
    private String experience;
    private String schedule;
    private String phoneNumber;

    public Employee(String fullName, String experience, String schedule, String phoneNumber) {
        this.fullName = fullName;
        this.experience = experience;
        this.schedule = schedule;
        this.phoneNumber = phoneNumber;
    }
}
