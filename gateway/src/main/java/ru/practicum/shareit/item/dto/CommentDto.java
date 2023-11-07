package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
public class CommentDto {
    @NotBlank
    private String text;
}
