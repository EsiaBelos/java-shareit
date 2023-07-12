package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Validated
@Builder
public class Item {
    private Long id;

    @NotBlank
    private String name;

    private String description;

    private Boolean isAvailable;

    private User owner;

    private ItemRequest request;
}
