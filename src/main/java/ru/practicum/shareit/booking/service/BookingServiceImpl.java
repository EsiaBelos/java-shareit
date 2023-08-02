package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public OutcomingBookingDto addBooking(long userId, BookingDto bookingDto) {
        User booker = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        if (userId == item.getOwner().getId()) {
            throw new UserNotFoundException("Владелец не может забронировать свою вещь");
        }
        if (item.getAvailable() && bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
            Booking savedBooking = bookingRepository.save(booking);
            return BookingMapper.toOutcomingBookingDto(savedBooking);
        }
        throw new InvalidBookingDtoException("Получены некорректные даты бронирования или вещь недоступна");
    }

    @Override
    public OutcomingBookingDto updateBooking(long userId, long bookingId, boolean available) { //обновить статус брони
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new IllegalArgumentException("Бронирование не находится в статусе WAITING");
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            booking.setStatus(available ? Status.APPROVED : Status.REJECTED);
            return BookingMapper.toOutcomingBookingDto(bookingRepository.save(booking));
        }
        throw new AccessDeniedException(String.format(
                "Пользователь id = %d не владеет вещью с id %d", userId, booking.getItem().getId()));
    }

    @Override
    public List<OutcomingBookingDto> getBookings(long userId, State stateEnum) { //Получение списка всех бронирований текущего пользователя
        checkUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (stateEnum) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByBooker_IdOrderByStartDesc(userId));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllCurrent(userId, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllPast(userId, LocalDateTime.now()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllFuture(userId, LocalDateTime.now()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED));
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toOutcomingBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OutcomingBookingDto> getBookingsForOwnedItems(long userId, State stateEnum) { //Получение списка бронирований для всех вещей текущего пользователя хотя бы 1 вещь
        checkUser(userId);
        List<Long> itemIds = itemRepository.findItemIds(userId);
        if (itemIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Booking> bookings = new ArrayList<>();
        switch (stateEnum) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByItem_IdInOrderByStartDesc(itemIds));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByItem_IdCurrent(itemIds, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByItem_IdInAndEndIsBeforeOrderByStartDesc(itemIds, LocalDateTime.now()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByItem_IdInAndStartIsAfterOrderByStartDesc(itemIds, LocalDateTime.now()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByItemId_IdInAndStatusOrderByStartDesc(itemIds, Status.WAITING));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByItemId_IdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED));
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toOutcomingBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public OutcomingBookingDto getBooking(long userId, long bookingId) { //Получение данных о конкретном бронировании владельцем или арендатором
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwner().getId()) {
            return BookingMapper.toOutcomingBookingDto(booking);
        }
        throw new AccessDeniedException(String.format("Пользователю id %d отказано в доступе", userId));
    }

    private User checkUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
    }

    private Item checkItem(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return item.get();
        }
        throw new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId));
    }

    private Booking checkBooking(long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        }
        throw new BookingNotFoundException(String.format("Бронирование id %d не найдено", bookingId));
    }
}
