package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class ItemCommentBookingDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemBooking lastBooking;

    private ItemBooking nextBooking;

    private final List<CommentDto> comments = new ArrayList<>();


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemBooking {
        private Long id;
        private Long bookerId;
    }
}
