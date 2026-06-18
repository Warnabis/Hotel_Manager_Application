package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicePayment {

    private int id;
    private int serviceId;
    private int paymentId;

    public ServicePayment(int serviceId, int paymentId) {
        this.serviceId = serviceId;
        this.paymentId = paymentId;
    }
}
