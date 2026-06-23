package com.warnabis.Hotel_SpringBoot_Application.service;

import com.warnabis.Hotel_SpringBoot_Application.dto.request.BookingRequestDto;
import com.warnabis.Hotel_SpringBoot_Application.dto.response.BookingResponseDto;
import com.warnabis.Hotel_SpringBoot_Application.exception.ResourceNotFoundException;
import com.warnabis.Hotel_SpringBoot_Application.mapper.BookingMapper;
import com.warnabis.Hotel_SpringBoot_Application.model.Booking;
import com.warnabis.Hotel_SpringBoot_Application.model.Guest;
import com.warnabis.Hotel_SpringBoot_Application.model.Payment;
import com.warnabis.Hotel_SpringBoot_Application.model.Room;
import com.warnabis.Hotel_SpringBoot_Application.repository.BookingRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.GuestRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.PaymentRepository;
import com.warnabis.Hotel_SpringBoot_Application.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    public Page<BookingResponseDto> filterWithPagination(Long id, Double price, String status,
                                                         LocalDate checkInDate, String duration,
                                                         Integer guestId, Pageable pageable) {
        return bookingRepository.findWithFilters(id, price, status, checkInDate, duration, guestId, pageable)
          .map(bookingMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public BookingResponseDto findById(Long id) {
        return bookingMapper.toResponseDto(getBookingOrThrow(id));
    }

    public BookingResponseDto create(BookingRequestDto request) {
        Booking booking = new Booking();
        booking.setPrice(request.getPrice());
        booking.setStatus(request.getStatus());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setDuration(request.getDuration());

        if (request.getGuestId() != null) {
            Guest guest = guestRepository.findById(request.getGuestId().longValue())
              .orElseThrow(() -> new ResourceNotFoundException("Гость", request.getGuestId()));
            booking.setGuest(guest);
        }

        if (request.getRoomIds() != null && !request.getRoomIds().isEmpty()) {
            List<Room> rooms = roomRepository.findAllById(
              request.getRoomIds().stream().map(Integer::longValue).toList()
            );
            booking.getRooms().addAll(rooms);
        }

        if (request.getPaymentIds() != null && !request.getPaymentIds().isEmpty()) {
            List<Payment> payments = paymentRepository.findAllById(
              request.getPaymentIds().stream().map(Integer::longValue).toList()
            );
            booking.getPayments().addAll(payments);
        }

        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    public BookingResponseDto update(Long id, BookingRequestDto request) {
        Booking booking = getBookingOrThrow(id);

        if (request.getPrice() != null) booking.setPrice(request.getPrice());
        if (request.getStatus() != null) booking.setStatus(request.getStatus());
        if (request.getCheckInDate() != null) booking.setCheckInDate(request.getCheckInDate());
        if (request.getDuration() != null) booking.setDuration(request.getDuration());

        if (request.getGuestId() != null) {
            Guest guest = guestRepository.findById(request.getGuestId().longValue())
              .orElseThrow(() -> new ResourceNotFoundException("Гость", request.getGuestId()));
            booking.setGuest(guest);
        }

        if (request.getRoomIds() != null) {
            booking.getRooms().clear();
            if (!request.getRoomIds().isEmpty()) {
                List<Room> rooms = roomRepository.findAllById(
                  request.getRoomIds().stream().map(Integer::longValue).toList()
                );
                booking.getRooms().addAll(rooms);
            }
        }

        if (request.getPaymentIds() != null) {
            booking.getPayments().clear();
            if (!request.getPaymentIds().isEmpty()) {
                List<Payment> payments = paymentRepository.findAllById(
                  request.getPaymentIds().stream().map(Integer::longValue).toList()
                );
                booking.getPayments().addAll(payments);
            }
        }

        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    public void delete(Long id) {
        bookingRepository.delete(getBookingOrThrow(id));
    }

    private Booking getBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Бронирование", id));
    }
}