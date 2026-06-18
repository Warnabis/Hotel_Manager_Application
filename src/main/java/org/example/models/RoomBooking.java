package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomBooking {

    private int id;
    private int roomId;
    private int bookingId;

    public RoomBooking(int roomId, int bookingId) {
        this.roomId = roomId;
        this.bookingId = bookingId;
    }
}
