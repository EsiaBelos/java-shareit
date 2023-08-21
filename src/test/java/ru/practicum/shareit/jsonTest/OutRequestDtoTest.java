package ru.practicum.shareit.jsonTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.OutRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OutRequestDtoTest {

    @Autowired
    private JacksonTester<OutRequestDto> jsonRequestDto;

    @Test
    void testOutRequestDto() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();

        OutRequestDto request = OutRequestDto.builder()
                .id(1L)
                .description("request description")
                .created(dateTime)
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Thing")
                .description("description of a thing")
                .available(true)
                .requestId(1L)
                .build();
        request.getItems().add(item);

        JsonContent<OutRequestDto> result = jsonRequestDto.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(request.getId())));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(request.getDescription());
        assertThat(result).extractingJsonPathStringValue(
                "$.created").isEqualTo(request.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue(
                "$.items.[0].id").satisfies((number ->
                assertThat(number.longValue()).isEqualTo(item.getId())));
    }
}
