package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public OutcomingBookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}") //Подтверждение или отклонение запроса на бронирование
    public OutcomingBookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public OutcomingBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping //Получение списка всех бронирований текущего пользователя
    public List<OutcomingBookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam String state,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner") //Получение списка бронирований для всех вещей текущего пользователя
    public List<OutcomingBookingDto> getBookingsForOwnedItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                              @RequestParam String state,
                                                              @RequestParam Integer from,
                                                              @RequestParam Integer size) {
        return bookingService.getBookingsForOwnedItems(userId, state, from, size);
    }
}
