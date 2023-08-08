package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
public class ItemRequestDto {

    @NotBlank
    private String description;
}
