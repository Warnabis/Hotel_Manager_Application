package org.example.models;

import lombok.*;


@Data
@NoArgsConstructor
public class Employee extends EntityWithId {
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
