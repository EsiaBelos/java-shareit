package ru.practicum.shareit.jsonTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OutComingBookingDtoTest {
    @Autowired
    private JacksonTester<OutcomingBookingDto> jsonBookingDto;

    @Test
    void testOutComingBookingDto() throws IOException {
        OutcomingBookingDto bookingDto = OutcomingBookingDto.builder()
                .item(new OutcomingBookingDto.BookingItemDto(1L, "Thing"))
                .booker(new OutcomingBookingDto.BookerDto(1L))
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Status.APPROVED)
                .build();

        JsonContent<OutcomingBookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(bookingDto.getId())));
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(
                bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(
                bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.item.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(bookingDto.getItem().getId())));
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(
                bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(bookingDto.getBooker().getId())));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(
                bookingDto.getStatus().toString());
    }
}
