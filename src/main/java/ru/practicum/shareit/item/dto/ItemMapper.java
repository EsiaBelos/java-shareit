package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public ItemCommentBookingDto toItemCommentBookingDto(Item item, Booking nextBooking, Booking lastBooking) {
        return ItemCommentBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(nextBooking != null ?
                        new ItemCommentBookingDto.ItemBooking(nextBooking.getId(), nextBooking.getBooker().getId()) : null)
                .lastBooking(lastBooking != null ?
                        new ItemCommentBookingDto.ItemBooking(lastBooking.getId(), lastBooking.getBooker().getId()) : null)
                .build();
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

    public OutcomingBookingDto.BookingItemDto toBookingItemDto(Item item) {
        return new OutcomingBookingDto.BookingItemDto(item.getId(), item.getName());
    }
}
