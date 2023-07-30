package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemUtil;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        log.debug("Item created id {}", item.getId());
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public CommentDto addComment(long userId, long itemId, RequestCommentDto commentDto) { //пользователь, который брал вещь в аренду, и только после окончания срока аренды
        User user = checkUser(userId);
        Item item = repository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        List<Booking> bookings = bookingRepository.findLastBookingByBooker(itemId, userId, LocalDateTime.now(), Status.APPROVED);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException(String.format("Пользователь id %d не может оставить комментарий", userId));
        }
        Comment comment = commentRepository.save(Comment.builder()
                .text(commentDto.getText())
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build());
        return ItemMapper.toCommentDto(comment);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        Item itemInstore = repository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        if (itemInstore.getOwner().getId() == userId) {
            Item itemUpdated = ItemUtil.test(itemInstore, itemDto);
            return ItemMapper.toItemDto(repository.save(itemUpdated));
        }
        throw new AccessDeniedException(String.format("Пользователь id = %d не владеет вещью с id %d", userId, itemId));
    }

    @Override
    public List<ItemCommentBookingDto> getItems(long ownerId) { //только владелец
        checkUser(ownerId);
        List<Item> items = repository.findAllByOwner_IdOrderById(ownerId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream().map(item -> {
                    ItemCommentBookingDto dto = ItemMapper.toItemCommentBookingDto(item, getNextBooking(item.getId()),
                            getLastBooking(item.getId()));
                    dto.getComments().addAll(getComments(item.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemCommentBookingDto getItemById(Long userId, long itemId) {
        checkUser(userId);
        Item item = repository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        List<CommentDto> comments = getComments(itemId);
        ItemCommentBookingDto dto;
        if (userId.equals(item.getOwner().getId())) {
            dto = ItemMapper.toItemCommentBookingDto(item, getNextBooking(itemId),
                    getLastBooking(itemId));
        } else {
            dto = ItemMapper.toItemCommentBookingDto(item, null, null);
        }
        dto.getComments().addAll(comments);
        return dto;
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        checkUser(userId);
        List<Item> items = repository.searchItems(text);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User checkUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return user;
    }

    private Booking getLastBooking(long itemId) {
        List<Booking> bookings = bookingRepository.findLastBooking(itemId, Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            return null;
        }
        return bookings.get(0);
    }

    private Booking getNextBooking(long itemId) {
        List<Booking> bookings = bookingRepository.findNextBooking(itemId, Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            return null;
        }
        return bookings.get(0);
    }

    private List<CommentDto> getComments(long itemId) {
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream().map(ItemMapper::toCommentDto).collect(Collectors.toList());
    }
}
