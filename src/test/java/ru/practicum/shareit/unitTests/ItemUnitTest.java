package ru.practicum.shareit.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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
class ItemUnitTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    RequestRepository requestRepository;
    User user;
    Item item;
    Booking lastBooking;
    Booking nextBooking;
    ItemRequest request;


    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, requestRepository);
        user = User.builder()
                .id(1L)
                .email("user@user.com")
                .name("Harry")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Book")
                .description("About friends")
                .available(false)
                .user(user)
                .build();
        lastBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .build();
        nextBooking = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user)
                .build();
        request = ItemRequest.builder().id(1L).build();
    }

    @Test
    void addItem_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                itemService.addItem(anyLong(), null));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void addComment_whenNoValidBookingFound() { //findLastBookingByBooker(itemId, userId, LocalDateTime.now(), Status.APPROVED);
        when(bookingRepository.findLastBookingByBooker(anyLong(), anyLong(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
                itemService.addComment(1L, 1L, null));
        verify(commentRepository, never()).save(new Comment());
    }

    @Test
    void updateItem_whenNoItemFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () ->
                itemService.updateItem(1L, null, 1L));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void updateItem_whenUserNotAnOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () ->
                itemService.updateItem(2L, null, 1L));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void getItemById_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                itemService.getItemById(anyLong(), 1L));
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void getItemById_whenItemNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () ->
                itemService.getItemById(user.getId(), anyLong()));
        verify(itemRepository, atMostOnce()).findById(anyLong());
    }

    @Test
    void getItemById_whenUserNotOwner_withCommentsEmpty() {
        user.setId(99L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemCommentBookingDto dto = itemService.getItemById(user.getId(), item.getId());
        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertNotNull(dto.getComments());
        assertEquals(0, dto.getComments().size());
    }

    @Test
    void searchItems_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                itemService.searchItems(anyLong(), null, 0, 10));
        verify(itemRepository, never()).searchItems(anyString(), any(Pageable.class));
    }

    @Test
    void searchItems_whenNoItemsFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.searchItems(anyString(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<ItemDto> items = itemService.searchItems(user.getId(), anyString(), 0, 2);
        verify(itemRepository, atMostOnce()).searchItems(anyString(), any(Pageable.class));
        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void getItems_whenNoUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.getItems(anyLong(), 0, 2));
        verify(itemRepository, never()).findAllByUserIdOrderById(anyLong(), any(Pageable.class));
    }

    @Test
    void getItems_whenNoItemsFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findAllByUserIdOrderById(user.getId(), PageRequest.of(0, 2, Sort.by("id"))))
                .thenReturn(Collections.emptyList());

        List<ItemCommentBookingDto> items = itemService.getItems(user.getId(), 0, 2);
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, atMostOnce()).findAllByUserIdOrderById(user.getId(), PageRequest.of(0, 2, Sort.by("id")));
        verify(bookingRepository, never()).findLastBookingList(anySet(),
                anyString(), any(LocalDateTime.class));
        verify(bookingRepository, never()).findNextBookingList(anySet(),
                anyString(), any(LocalDateTime.class));
    }

    @Test
    void testItemMapper() {
        assertThrows(NullPointerException.class, () -> ItemMapper.toItemDto(null));
        assertThrows(NullPointerException.class, () ->
                ItemMapper.toItemCommentBookingDto(null, null, null, Collections.emptyList()));

        ItemDto withoutRequest = ItemMapper.toItemDto(item);
        assertNotNull(withoutRequest);
        assertNull(withoutRequest.getRequestId());

        ItemCommentBookingDto noBookings = ItemMapper.toItemCommentBookingDto(item, null, null,
                Collections.emptyList());
        assertNotNull(noBookings);
        assertEquals(item.getId(), noBookings.getId());
        assertEquals(item.getName(), noBookings.getName());
        assertNull(noBookings.getLastBooking());
        assertNull(noBookings.getNextBooking());

        ItemCommentBookingDto withBookings = ItemMapper.toItemCommentBookingDto(item, nextBooking, lastBooking,
                Collections.emptyList());
        assertNotNull(withBookings);
        assertNotNull(withBookings.getNextBooking());
        assertNotNull(withBookings.getLastBooking());
        assertEquals(nextBooking.getId(), withBookings.getNextBooking().getId());
        assertEquals(lastBooking.getId(), withBookings.getLastBooking().getId());

        assertNotNull(ItemMapper.toItemCommentBookingDto(item, null, lastBooking, Collections.emptyList()));
        assertNotNull(ItemMapper.toItemCommentBookingDto(item, nextBooking, null, Collections.emptyList()));

        item.setRequest(request);
        ItemDto withRequest = ItemMapper.toItemDto(item);
        assertNotNull(withRequest);
        assertNotNull(withRequest.getRequestId());
        assertEquals(request.getId(), withRequest.getRequestId());
    }
}