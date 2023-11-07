package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public ItemCommentBookingDto toItemCommentBookingDto(Item item, Booking nextBooking, Booking lastBooking,
                                                         List<CommentDto> comments) {
        ItemCommentBookingDto dto = ItemCommentBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(nextBooking != null ?
                        new ItemCommentBookingDto.ItemBooking(nextBooking.getId(),
                                nextBooking.getBooker().getId()) : null)
                .lastBooking(lastBooking != null ?
                        new ItemCommentBookingDto.ItemBooking(lastBooking.getId(),
                                lastBooking.getBooker().getId()) : null)
                .build();
        if (!comments.isEmpty()) {
            dto.getComments().addAll(comments);
        }
        return dto;
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getCreated(),
                comment.getAuthor().getName());
    }

    public List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        List<CommentDto> commentDtos = comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());
        return commentDtos;
    }

    public OutcomingBookingDto.BookingItemDto toBookingItemDto(Item item) {
        return new OutcomingBookingDto.BookingItemDto(item.getId(), item.getName());
    }
}
