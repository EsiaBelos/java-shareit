package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Validated
@ToString
public class BookingDto {
    @NotNull
    private long itemId;

    @NotNull
    @Future
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;
}
