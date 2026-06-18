package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomEmployee {

    private int id;
    private int roomId;
    private int employeeId;

    public RoomEmployee(int roomId, int employeeId) {
        this.roomId = roomId;
        this.employeeId = employeeId;
    }
}
