package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemCommentBookingDto> getItems(long userId, Integer from, Integer size);

    ItemCommentBookingDto getItemById(Long userId, Long itemId);

    List<ItemDto> searchItems(long userId, String text, Integer from, Integer size);

    CommentDto addComment(long userId, long itemId, InCommentDto commentDto);
}
