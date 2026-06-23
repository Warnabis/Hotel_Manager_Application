package com.warnabis.hotel_springboot_application.mapper;

import com.warnabis.hotel_springboot_application.dto.request.BookingRequestDto;
import com.warnabis.hotel_springboot_application.dto.response.BookingResponseDto;
import com.warnabis.hotel_springboot_application.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final GuestMapper guestMapper;
    private final RoomMapper roomMapper;
    private final PaymentMapper paymentMapper;

    public Booking toEntity(BookingRequestDto dto) {
        if (dto == null) return null;
        Booking booking = new Booking();
        booking.setPrice(dto.getPrice());
        booking.setStatus(dto.getStatus());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setDuration(dto.getDuration());

        return booking;
    }

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) return null;
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setPrice(booking.getPrice());
        dto.setStatus(booking.getStatus());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setDuration(booking.getDuration());

        if (booking.getGuest() != null) {
            dto.setGuest(guestMapper.toResponseDto(booking.getGuest()));
        }

        if (booking.getRooms() != null) {
            dto.setRooms(roomMapper.toResponseDtoList(booking.getRooms()));
        }

        if (booking.getPayments() != null) {
            dto.setPayments(paymentMapper.toResponseDtoList(booking.getPayments()));
        }

        return dto;
    }

    public void updateEntity(BookingRequestDto dto, Booking booking) {
        if (dto == null || booking == null) return;
        if (dto.getPrice() != null) booking.setPrice(dto.getPrice());
        if (dto.getStatus() != null) booking.setStatus(dto.getStatus());
        if (dto.getCheckInDate() != null) booking.setCheckInDate(dto.getCheckInDate());
        if (dto.getDuration() != null) booking.setDuration(dto.getDuration());

    }
}