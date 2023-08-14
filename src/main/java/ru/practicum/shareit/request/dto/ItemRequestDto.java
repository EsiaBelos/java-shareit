package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
@Accessors(chain = true)
public class ItemRequestDto {

    @NotBlank
    private String description;
}
