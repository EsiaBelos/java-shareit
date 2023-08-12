package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemUtil;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != null ) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new RequestNotFoundException(String.format("Запрос не найден id = %d", itemDto.getRequestId())));
            item.setRequest(request);
        }
        item.setUser(owner);
        log.debug("Item created id {}", item.getId());
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public CommentDto addComment(long userId, long itemId, InCommentDto commentDto) { //пользователь, который брал вещь в аренду, и только после окончания срока аренды
        List<Booking> bookings = bookingRepository.findLastBookingByBooker(itemId, userId, LocalDateTime.now(), Status.APPROVED);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException(String.format("Пользователь id %d не может оставить комментарий", userId));
        }
        User user = checkUser(userId);
        Item item = repository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
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
        if (itemInstore.getUser().getId() == userId) {
            Item itemUpdated = ItemUtil.test(itemInstore, itemDto);
            return ItemMapper.toItemDto(repository.save(itemUpdated));
        }
        throw new AccessDeniedException(String.format("Пользователь id = %d не владеет вещью с id %d", userId, itemId));
    }

    @Override
    public List<ItemCommentBookingDto> getItems(long ownerId) { //только владелец
        checkUser(ownerId);
        List<Item> itemList = repository.findAllByUserIdOrderById(ownerId);
        if (itemList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Item> itemMap = itemList.stream().collect(Collectors.toMap(Item::getId, Function.identity()));

        List<Booking> lastBookinglist =
                bookingRepository.findLastBookingList(itemMap.keySet(), Status.APPROVED.toString(), LocalDateTime.now());
        Map<Long, Booking> lastBookingMap = lastBookinglist.stream().collect(Collectors.toMap(
                booking -> booking.getItem().getId(), Function.identity()));

        List<Booking> nextBookingList =
                bookingRepository.findNextBookingList(itemMap.keySet(), Status.APPROVED.toString(), LocalDateTime.now());
        Map<Long, Booking> nextBookingMap = nextBookingList.stream().collect(Collectors.toMap(
                booking -> booking.getItem().getId(), Function.identity()));

        return itemList.stream().map(item ->
                        ItemMapper.toItemCommentBookingDto(item, nextBookingMap.get(item.getId()), lastBookingMap.get(item.getId())))
                        .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public ItemCommentBookingDto getItemById(Long userId, Long itemId) {
        checkUser(userId);
        Item item = repository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        List<CommentDto> comments = getComments(itemId);
        ItemCommentBookingDto dto;
        if (userId.equals(item.getUser().getId())) {
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
