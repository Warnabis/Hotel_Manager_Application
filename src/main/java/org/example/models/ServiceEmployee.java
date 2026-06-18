package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEmployee {

    private int id;
    private int employeeId;
    private int serviceId;

    public ServiceEmployee(int employeeId, int serviceId) {
        this.employeeId = employeeId;
        this.serviceId = serviceId;
    }

}
