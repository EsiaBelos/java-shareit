package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> getItems(long userId);

    Item getItemById(long userId, long itemId);

    List<ItemDto> searchItems(long userId, String text);
}
