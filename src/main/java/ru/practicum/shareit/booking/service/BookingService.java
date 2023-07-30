package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;

import java.util.List;

public interface BookingService {
    OutcomingBookingDto addBooking(long userId, BookingDto bookingDto);

    OutcomingBookingDto updateBooking(long userId, long bookingId, boolean available);


    List<OutcomingBookingDto> getBookings(long userId, String state);

    List<OutcomingBookingDto> getBookingsForOwnedItems(long userId, String state);

    OutcomingBookingDto getBooking(long userId, long bookingId);
}
