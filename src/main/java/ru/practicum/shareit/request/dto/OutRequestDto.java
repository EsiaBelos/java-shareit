package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@Setter
public class OutRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;

    private final Set<ItemDto> items = new HashSet<>();
}
