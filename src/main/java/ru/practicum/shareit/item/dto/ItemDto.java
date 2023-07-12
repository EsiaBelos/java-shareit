package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Getter
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean isAvailable;
}
