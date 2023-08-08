package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestMapper {


    public ItemRequest toItemRequest(ItemRequestDto requestDto, User user) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
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
