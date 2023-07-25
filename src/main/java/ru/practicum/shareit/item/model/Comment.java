package ru.practicum.shareit.item.model;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class Comment {
    private Long id;

    private String text;

    private Item item;

    private User author;

    private LocalDateTime created;

}
