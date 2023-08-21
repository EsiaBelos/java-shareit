package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    OutcomingBookingDto addBooking(long userId, BookingDto bookingDto);

    OutcomingBookingDto updateBooking(long userId, long bookingId, boolean available);


    List<OutcomingBookingDto> getBookings(long userId, State state, Integer from, Integer size);

    List<OutcomingBookingDto> getBookingsForOwnedItems(long userId, State state, Integer from, Integer size);

    OutcomingBookingDto getBooking(long userId, long bookingId);
}
