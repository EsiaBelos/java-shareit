package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Builder
@Getter
public class OutcomingBookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;

    private BookerDto booker;

    private BookingItemDto item;


    @Getter
    @AllArgsConstructor
    public static class BookerDto {
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    public static class BookingItemDto {
        private Long id;
        private String name;
    }
}
