package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class ItemRequestMapper {


    public ItemRequest toItemRequest(ItemRequestDto requestDto, User user) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .build();
    }

    public static OutRequestDto toOutRequestDto(ItemRequest itemRequest) {
        return OutRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
