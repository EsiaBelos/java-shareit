package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Validated
public class UserDto {
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
