package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> getItems(long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> searchItems(long userId, String text);
}
