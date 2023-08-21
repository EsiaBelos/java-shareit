package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public class CommentDto {
    private Long id;

    private String text;

    private LocalDateTime created;

    private String authorName;
}
