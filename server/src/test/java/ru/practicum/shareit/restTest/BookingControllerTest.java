package ru.practicum.shareit.restTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidBookingDtoException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private static BookingDto bookingDto;
    private static OutcomingBookingDto outcomingBookingDto;

    @BeforeAll
    static void setUp() {
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        outcomingBookingDto = OutcomingBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(new OutcomingBookingDto.BookerDto(1L))
                .item(new OutcomingBookingDto.BookingItemDto(1L, "name"))
                .build();
    }

    @Test
    @SneakyThrows
    void addBooking() {
        doReturn(outcomingBookingDto)
                .when(bookingService)
                .addBooking(anyLong(), any(BookingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(outcomingBookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(outcomingBookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id",
                        is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",
                        is(outcomingBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",
                        is(outcomingBookingDto.getItem().getName())));
    }

    @Test
    @SneakyThrows
    void addBooking_whenFailed() {
        doThrow(UserNotFoundException.class)
                .when(bookingService)
                .addBooking(anyLong(), any(BookingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isNotFound());

        doThrow(InvalidBookingDtoException.class)
                .when(bookingService)
                .addBooking(anyLong(), any(BookingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        outcomingBookingDto.setStatus(Status.APPROVED);
        doReturn(outcomingBookingDto)
                .when(bookingService)
                .updateBooking(anyLong(), anyLong(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(outcomingBookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(outcomingBookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id",
                        is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",
                        is(outcomingBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",
                        is(outcomingBookingDto.getItem().getName())));
    }

    @Test
    @SneakyThrows
    void updateBooking_whenFailed() {
        doThrow(IllegalArgumentException.class)
                .when(bookingService)
                .updateBooking(anyLong(), anyLong(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        doThrow(AccessDeniedException.class)
                .when(bookingService)
                .updateBooking(anyLong(), anyLong(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getBooking() {
        doReturn(outcomingBookingDto)
                .when(bookingService)
                .getBooking(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(outcomingBookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(outcomingBookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id",
                        is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",
                        is(outcomingBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",
                        is(outcomingBookingDto.getItem().getName())));
    }

    @Test
    @SneakyThrows
    void getBooking_whenFailed() {
        doThrow(AccessDeniedException.class)
                .when(bookingService)
                .getBooking(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

        doThrow(BookingNotFoundException.class)
                .when(bookingService)
                .getBooking(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

        doThrow(UserNotFoundException.class)
                .when(bookingService)
                .getBooking(anyLong(), anyLong());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getBookings() {
        doReturn(List.of(outcomingBookingDto))
                .when(bookingService)
                .getBookings(anyLong(), anyString(), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(outcomingBookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(outcomingBookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status",
                        is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].booker.id",
                        is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id",
                        is(outcomingBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name",
                        is(outcomingBookingDto.getItem().getName())));
    }

    @Test
    @SneakyThrows
    void getBookingsForOwnedItems() {
        doReturn(List.of(outcomingBookingDto))
                .when(bookingService)
                .getBookingsForOwnedItems(anyLong(), anyString(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(outcomingBookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(outcomingBookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status",
                        is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].booker.id",
                        is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id",
                        is(outcomingBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name",
                        is(outcomingBookingDto.getItem().getName())));
    }
}