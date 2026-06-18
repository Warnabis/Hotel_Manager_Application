package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceGuest {

    private int id;
    private int guestId;
    private int serviceId;

    public ServiceGuest(int guestId, int serviceId) {
        this.guestId = guestId;
        this.serviceId = serviceId;
    }
}
