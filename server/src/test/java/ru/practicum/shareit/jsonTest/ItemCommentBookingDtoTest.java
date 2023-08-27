package ru.practicum.shareit.jsonTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemCommentBookingDtoTest {

    @Autowired
    private JacksonTester<ItemCommentBookingDto> jsonItemDto;

    @Test
    void testItemCommentBookingDto() throws IOException {
        ItemCommentBookingDto.ItemBooking lastBooking = new ItemCommentBookingDto.ItemBooking(1L, 1L);
        ItemCommentBookingDto.ItemBooking nextBooking = new ItemCommentBookingDto.ItemBooking(2L, 2L);

        CommentDto commentDto = new CommentDto(1L, "Comment",
                LocalDateTime.now().minusDays(1), "Booker");

        ItemCommentBookingDto item = ItemCommentBookingDto.builder()
                .id(1L)
                .name("Thing")
                .description("description of a thing")
                .available(true)
                .nextBooking(nextBooking)
                .lastBooking(lastBooking)
                .build();
        item.getComments().add(commentDto);

        JsonContent<ItemCommentBookingDto> result = jsonItemDto.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(item.getId())));
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue(
                "$.lastBooking.id").satisfies((number ->
                assertThat(number.longValue()).isEqualTo(item.getLastBooking().getId())));
        assertThat(result).extractingJsonPathNumberValue(
                "$.nextBooking.id").satisfies((number ->
                assertThat(number.longValue()).isEqualTo(item.getNextBooking().getId())));
        assertThat(result).extractingJsonPathStringValue(
                "$.comments.[0].text").isEqualTo(item.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue(
                "$.comments.[0].created").isEqualTo(item.getComments().get(0).getCreated().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}