package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, User booker, Item item) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(Status.WAITING)
                .booker(booker)
                .item(item)
                .build();
    }

    public OutcomingBookingDto toOutcomingBookingDto(Booking booking) {
        return OutcomingBookingDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toBookingItemDto(booking.getItem()))
                .booker(UserMapper.toBookerDto(booking.getBooker()))
                .build();
    }
}
