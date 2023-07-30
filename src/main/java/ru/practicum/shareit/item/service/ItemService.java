package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemCommentBookingDto> getItems(long userId);

    ItemCommentBookingDto getItemById(Long userId, long itemId);

    List<ItemDto> searchItems(long userId, String text);

    CommentDto addComment(long userId, long itemId, RequestCommentDto commentDto);
}
