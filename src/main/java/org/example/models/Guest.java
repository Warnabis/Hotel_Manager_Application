package org.example.models;

@lombok.Data
@lombok.NoArgsConstructor
public class Guest extends EntityWithId {
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
