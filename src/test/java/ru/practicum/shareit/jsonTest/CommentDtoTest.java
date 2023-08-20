package ru.practicum.shareit.jsonTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jsonItemDto;

    @Test
    void testCommentDto() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "Comment", dateTime, "Booker");

        JsonContent<CommentDto> result = jsonItemDto.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").satisfies(
                (number -> assertThat(number.longValue()).isEqualTo(commentDto.getId())));
        assertThat(result).extractingJsonPathStringValue(
                "$.text").isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue(
                "$.created").isEqualTo(commentDto.getCreated().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
    }
}
