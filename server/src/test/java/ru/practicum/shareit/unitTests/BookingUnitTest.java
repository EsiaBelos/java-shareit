package ru.practicum.shareit.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingUnitTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    User user;
    User user2;
    Item item;
    Item item2;
    Booking firstBooking;
    Booking secondBooking;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user = User.builder()
                .id(1L)
                .email("user@user.com")
                .name("Harry")
                .build();
        user2 = User.builder()
                .id(2L)
                .email("user@user.com")
                .name("Fuser")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Book")
                .description("About friends")
                .available(false)
                .user(user)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("Book")
                .description("About friends")
                .available(true)
                .user(user2)
                .build();
        firstBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        secondBooking = Booking.builder()
                .id(2L)
                .item(item2)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 9, 21, 12, 0),
                LocalDateTime.of(2023, 9, 20, 12, 0));
    }

    @Test
    void addBooking_whenNoUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(anyLong(), null));
    }

    @Test
    void addBooking_whenNoItemFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () ->
                bookingService.addBooking(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void addBooking_whenUserIsAnOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(UserNotFoundException.class, () ->
                bookingService.addBooking(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void addBooking_whenItemNotAvailable() {
        user.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(InvalidBookingDtoException.class, () ->
                bookingService.addBooking(anyLong(), bookingDto));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void addBooking_whenStartAfterEnd() {
        user.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        item.setAvailable(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(InvalidBookingDtoException.class, () ->
                bookingService.addBooking(anyLong(), bookingDto));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void updateBooking_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                bookingService.updateBooking(anyLong(), 1L, true));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void updateBooking_whenBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.updateBooking(user.getId(), anyLong(), true));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void updateBooking_whenNotWaitingStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(firstBooking));

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.updateBooking(user.getId(), firstBooking.getId(), true));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void updateBooking_whenNotItemOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondBooking));

        assertThrows(AccessDeniedException.class, () ->
                bookingService.updateBooking(user.getId(), secondBooking.getId(), true));
        verify(bookingRepository, never()).save(firstBooking);
    }

    @Test
    void getBookings_whenNoUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                bookingService.getBookings(anyLong(), "ALL", 0, 10));
        verify(bookingRepository, never()).findAllByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookings_whenNoBookingsFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<OutcomingBookingDto> bookings = bookingService.getBookings(user.getId(),
                "ALL", 1, 2);
        verify(bookingRepository, atMostOnce()).findAllByBooker_Id(anyLong(), any(Pageable.class));
        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }

    @Test
    void getBookingsForOwnedItems_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                bookingService.getBookingsForOwnedItems(anyLong(), "ALL", 0, 10));
        verify(bookingRepository, never()).findAllByItem_IdIn(anyList(), any(Pageable.class));
    }

    @Test
    void getBookingsForOwnedItems_whenItemsNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemIds(anyLong()))
                .thenReturn(Collections.emptyList());

        List<OutcomingBookingDto> dtos = bookingService.getBookingsForOwnedItems(user.getId(), "ALL", 0, 10);
        verify(bookingRepository, never()).findAllByItem_IdIn(anyList(), any(Pageable.class));
        assertNotNull(dtos);
        assertEquals(0, dtos.size());
    }

    @Test
    void getBookingsForOwnedItems_whenBookingsNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemIds(anyLong()))
                .thenReturn(List.of(1L, 2L));
        when(bookingRepository.findAllByItem_IdIn(anyList(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<OutcomingBookingDto> dtos = bookingService.getBookingsForOwnedItems(user.getId(), "ALL", 0, 10);
        verify(bookingRepository, atMostOnce()).findAllByItem_IdIn(anyList(), any(Pageable.class));
        assertNotNull(dtos);
        assertEquals(0, dtos.size());
    }

    @Test
    void getBooking_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                bookingService.getBooking(anyLong(), 1L));
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void getBooking_whenBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.getBooking(user.getId(), anyLong()));
        verify(bookingRepository, atMostOnce()).findById(anyLong());
    }

    @Test
    void getBooking_whenUserNeitherBookerNorOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondBooking));

        assertThrows(AccessDeniedException.class, () ->
                bookingService.getBooking(user.getId(), firstBooking.getId()));
        verify(bookingRepository, atMostOnce()).findById(anyLong());
    }
}