package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePosition {

    private int id;
    private int employeeId;
    private int positionId;

    public EmployeePosition(int employeeId, int positionId) {
        this.employeeId = employeeId;
        this.positionId = positionId;
    }

}
