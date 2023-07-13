package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> getItems(long userId);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> searchItems(long userId, String text);
}
