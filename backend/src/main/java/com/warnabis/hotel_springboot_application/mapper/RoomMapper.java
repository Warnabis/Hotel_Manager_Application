package com.warnabis.hotel_springboot_application.mapper;

import com.warnabis.hotel_springboot_application.dto.request.RoomRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.RoomResponseDto;
import com.warnabis.hotel_springboot_application.model.Room;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequestDto dto) {
        if (dto == null) return null;
        Room room = new Room();
        room.setFloor(dto.getFloor());
        room.setType(dto.getType());
        room.setStatus(dto.getStatus());
        room.setPrice(dto.getPrice());
        return room;
    }

    public RoomResponseDto toResponseDto(Room room) {
        if (room == null) return null;
        RoomResponseDto dto = new RoomResponseDto();
        dto.setId(room.getId());
        dto.setFloor(room.getFloor());
        dto.setType(room.getType());
        dto.setStatus(room.getStatus());
        dto.setPrice(room.getPrice());
        return dto;
    }

    public List<RoomResponseDto> toResponseDtoList(List<Room> rooms) {
        if (rooms == null) return List.of();
        return rooms.stream()
          .map(this::toResponseDto)
          .toList();
    }

    public void updateEntity(RoomRequestDto dto, Room room) {
        if (dto == null || room == null) return;
        if (dto.getFloor() != null) room.setFloor(dto.getFloor());
        if (dto.getType() != null) room.setType(dto.getType());
        if (dto.getStatus() != null) room.setStatus(dto.getStatus());
        if (dto.getPrice() != null) room.setPrice(dto.getPrice());
    }
}