package org.example.models;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Guest {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String status;

    public Guest(String fullName, String phoneNumber, String email, String status) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.status = status;
    }
}
