package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.OutRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.driver-class-name=org.h2.Driver"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationWithDBTest {

    @Autowired
    private final UserServiceImpl userService;

    @Autowired
    private final ItemServiceImpl itemService;

    @Autowired
    private final BookingServiceImpl bookingService;

    @Autowired
    private final RequestServiceImpl requestService;
    ItemDto itemDtoInput1 = ItemDto.builder()
            .name("Гарри Поттер и Тайная комната")
            .description("Книга про волшебство")
            .available(true)
            .build();
    ItemDto itemDtoInput2 = ItemDto.builder()
            .available(false)
            .name("Винни Пух и все-все-все")
            .description("Книга про друзей")
            .build();
    ItemDto itemDtoInput3 = ItemDto.builder()
            .available(true)
            .name("Принтер Canon")
            .description("Черно-белая печать")
            .build();
    ItemRequestDto itemRequestDto1 = new ItemRequestDto().setDescription("Книга про Винни Пуха");
    ItemRequestDto itemRequestDto2 = new ItemRequestDto().setDescription("Самокат десткий");
    long userId1 = 1L;
    long userId2 = 3L;

    @Test
    @Order(1)
    void contextLoads() {
        assertNotNull(userService);
        assertNotNull(itemService);
        assertNotNull(bookingService);
        assertNotNull(requestService);
    }

    @Test
    @Order(2)
    void addUpdateGetByIdUser() {
        UserDto dtoInput = UserDto.builder()
                .name("Harry")
                .email("user@user.com")
                .build();
        UserDto user = userService.addUser(dtoInput); //ADD

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals(dtoInput.getName(), user.getName());
        assertEquals(dtoInput.getEmail(), user.getEmail());

        UserDto receivedUser = userService.getUserById(user.getId()); //GET
        assertNotNull(receivedUser);
        assertEquals(1L, receivedUser.getId());
        assertEquals(user.getName(), receivedUser.getName());
        assertEquals(user.getEmail(), receivedUser.getEmail());

        UserDto updateUserDto_WithName = UserDto.builder()
                .name("Harry")
                .build();

        UserDto updatedUser_WithName = userService.updateUser(updateUserDto_WithName, user.getId());
        assertNotNull(updatedUser_WithName);
        assertEquals(1L, updatedUser_WithName.getId());
        assertEquals(updateUserDto_WithName.getName(), updatedUser_WithName.getName());
        assertEquals(user.getEmail(), updatedUser_WithName.getEmail());

        UserDto updateUserDto_WithEmail = UserDto.builder()
                .email("harry@user.com")
                .build();

        UserDto updatedUser_WithEmail = userService.updateUser(updateUserDto_WithEmail, user.getId());
        assertNotNull(updatedUser_WithEmail);
        assertEquals(1L, updatedUser_WithEmail.getId());
        assertEquals(updatedUser_WithName.getName(), updatedUser_WithEmail.getName());
        assertEquals(updateUserDto_WithEmail.getEmail(), updatedUser_WithEmail.getEmail());

        UserDto fullDto = UserDto.builder()
                .name("Potter")
                .email("harry@potter.com")
                .build();
        UserDto updatedUser_Full = userService.updateUser(fullDto, user.getId());
        assertNotNull(updatedUser_Full);
        assertEquals(1L, updatedUser_Full.getId());
        assertEquals(fullDto.getName(), updatedUser_Full.getName());
        assertEquals(fullDto.getEmail(), updatedUser_Full.getEmail());
    }

    @Test
    @Order(3)
    void getUsersAndDelete() {
        UserDto dto = UserDto.builder()
                .name("Winnie")
                .email("winnie@pooh.com")
                .build();
        UserDto user = userService.addUser(dto);
        List<UserDto> users = userService.getUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(2L, users.get(1).getId());
        assertEquals(user.getName(), users.get(1).getName());
        assertEquals(user.getEmail(), users.get(1).getEmail());

        userService.deleteUser(2L);
        List<UserDto> usersAfterDelete = userService.getUsers();

        assertNotNull(usersAfterDelete);
        assertEquals(1, usersAfterDelete.size());
        assertEquals(1L, usersAfterDelete.get(0).getId());

        userService.addUser(dto);
    }

    @Test
    @Order(4)
    void addItem_WithoutRequest() {
        ItemDto savedItem = itemService.addItem(1L, itemDtoInput1);

        assertNotNull(savedItem);
        assertEquals(1L, savedItem.getId());
        assertEquals(itemDtoInput1.getName(), savedItem.getName());
        assertEquals(itemDtoInput1.getDescription(), savedItem.getDescription());
        assertEquals(itemDtoInput1.getAvailable(), savedItem.getAvailable());
        assertNull(savedItem.getRequestId());
    }

    @Test
    @Order(5)
    void updateItem_WithoutRequest() {
        itemDtoInput1.setName("Гарри Поттер и Философский камень");
        itemDtoInput1.setDescription("Книга про друзей");

        ItemDto updatedItem = itemService.updateItem(1L, itemDtoInput1, 1L);
        assertNotNull(updatedItem);
        assertEquals(1L, updatedItem.getId());
        assertEquals(itemDtoInput1.getName(), updatedItem.getName());
        assertEquals(itemDtoInput1.getDescription(), updatedItem.getDescription());
        assertEquals(itemDtoInput1.getAvailable(), updatedItem.getAvailable());
        assertNull(updatedItem.getRequestId());
    }

    @Test
    @Order(6)
    void addRequest() {
        OutRequestDto outRequestDto = requestService.addRequest(1L, itemRequestDto1);

        assertNotNull(outRequestDto);
        assertEquals(1L, outRequestDto.getId());
        assertEquals(itemRequestDto1.getDescription(), outRequestDto.getDescription());
        assertNotNull(outRequestDto.getItems());
        assertTrue(ChronoUnit.MILLIS.between(LocalDateTime.now(),
                outRequestDto.getCreated()) < 200);
    }

    @Test
    @Order(7)
    void getRequestById_withoutItems() {
        OutRequestDto outRequestDto = requestService.getRequestById(1L, 1L);

        assertNotNull(outRequestDto);
        assertEquals(1L, outRequestDto.getId());
        assertEquals(itemRequestDto1.getDescription(), outRequestDto.getDescription());
        assertNotNull(outRequestDto.getItems());
        assertEquals(0, outRequestDto.getItems().size());
        assertTrue(ChronoUnit.MILLIS.between(LocalDateTime.now(),
                outRequestDto.getCreated()) < 200);
    }

    @Test
    @Order(8)
    void getRequestsByRequestor() {
        OutRequestDto dto = requestService.addRequest(userId1, itemRequestDto2);
        List<OutRequestDto> requests = requestService.getRequestsByRequestor(userId1);

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertEquals(dto.getId(), requests.get(0).getId());
        assertEquals(dto.getDescription(), requests.get(0).getDescription());
        assertEquals(dto.getCreated(), requests.get(0).getCreated());
        assertNotNull(requests.get(0).getItems());
        assertEquals(0, requests.get(0).getItems().size());
        assertTrue(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
    }

    @Test
    @Order(9)
    void addItem_WithRequest() {
        Long requestId = 1L;

        itemDtoInput2.setRequestId(requestId);
        ItemDto savedItem = itemService.addItem(userId2, itemDtoInput2);

        assertNotNull(savedItem);
        assertEquals(2L, savedItem.getId());
        assertEquals(itemDtoInput2.getName(), savedItem.getName());
        assertEquals(itemDtoInput2.getDescription(), savedItem.getDescription());
        assertEquals(itemDtoInput2.getAvailable(), savedItem.getAvailable());
        assertEquals(requestId, savedItem.getRequestId());
    }

    @Test
    @Order(10)
    void updateItem_WithRequest() {
        long itemId = 2L;
        itemDtoInput2.setAvailable(true);
        itemDtoInput2.setRequestId(2L);

        ItemDto updatedItem = itemService.updateItem(3L, itemDtoInput2, itemId);

        assertNotNull(updatedItem);
        assertEquals(itemId, updatedItem.getId());
        assertEquals(itemDtoInput2.getName(), updatedItem.getName());
        assertEquals(itemDtoInput2.getDescription(), updatedItem.getDescription());
        assertEquals(itemDtoInput2.getAvailable(), updatedItem.getAvailable());
        assertEquals(2L, updatedItem.getRequestId());

        List<OutRequestDto> requests = requestService.getRequestsByRequestor(userId1);

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertNotNull(requests.get(0).getItems());
        assertEquals(1, requests.get(0).getItems().size());
        assertTrue(requests.get(0).getItems().stream().anyMatch(itemDto -> updatedItem.getId().equals(itemDto.getId())));
    }

    @Test
    @Order(11)
    void getRequestById_withItems() {
        long itemId = 2L;
        long requestId = 2L;
        OutRequestDto outRequestDto = requestService.getRequestById(userId1, requestId);

        assertNotNull(outRequestDto);
        assertEquals(requestId, outRequestDto.getId());
        assertEquals(itemRequestDto2.getDescription(), outRequestDto.getDescription());
        assertTrue(ChronoUnit.MILLIS.between(LocalDateTime.now(),
                outRequestDto.getCreated()) < 200);

        assertNotNull(outRequestDto.getItems());
        assertEquals(1, outRequestDto.getItems().size());

        assertTrue(outRequestDto.getItems().stream()
                .anyMatch(itemDto -> itemDto.getId().equals(itemId)));
        assertTrue(outRequestDto.getCreated().isBefore(LocalDateTime.now()));
    }

    @Test
    @Order(12)
    void getAllRequests() {
        List<OutRequestDto> requestDtos = requestService.getAllRequests(3L, 0, 1);

        assertNotNull(requestDtos);
        assertEquals(1, requestDtos.size());

        OutRequestDto element1 = requestDtos.get(0);
        assertEquals(2L, element1.getId());
        assertEquals(itemRequestDto2.getDescription(), element1.getDescription());
        assertTrue(ChronoUnit.MILLIS.between(LocalDateTime.now(),
                element1.getCreated()) < 200);

        assertNotNull(element1.getItems());
        assertEquals(1, element1.getItems().size());
        assertTrue(element1.getItems().stream()
                .anyMatch(itemDto -> itemDto.getId().equals(2L)));
    }

    @Test
    @Order(13)
    void addBooking() {
        long itemId = 2L;
        BookingDto bookingDtoInput = new BookingDto(itemId,
                LocalDateTime.of(2023, 9, 21, 12, 0),
                LocalDateTime.of(2023, 9, 30, 12, 0));

        OutcomingBookingDto booking = bookingService.addBooking(userId1, bookingDtoInput);

        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertEquals(Status.WAITING, booking.getStatus());
        assertEquals(bookingDtoInput.getStart(), booking.getStart());
        assertEquals(bookingDtoInput.getEnd(), booking.getEnd());
        assertEquals(userId1, booking.getBooker().getId());
        assertEquals(itemId, booking.getItem().getId());
    }

    @Test
    @Order(14)
    void updateBooking_Approved() {
        long ownerId = userId2;
        long bookerId = userId1;
        long itemId = 2L;
        long bookingId = 1L;

        OutcomingBookingDto booking = bookingService.updateBooking(ownerId, bookingId, true);

        assertNotNull(booking);
        assertEquals(bookingId, booking.getId());
        assertEquals(Status.APPROVED, booking.getStatus());
        assertEquals(bookerId, booking.getBooker().getId());
        assertEquals(itemId, booking.getItem().getId());
        assertEquals("Винни Пух и все-все-все", booking.getItem().getName());
    }

    @Test
    @Order(15)
    void updateBooking_Rejected() {
        long bookerId = userId2;
        long ownerId = userId1;
        long itemId = 1L;
        BookingDto bookingDtoInput = new BookingDto(itemId,
                LocalDateTime.of(2023, 10, 21, 12, 0),
                LocalDateTime.of(2023, 10, 30, 12, 0));

        OutcomingBookingDto bookingSaved = bookingService.addBooking(bookerId, bookingDtoInput);

        OutcomingBookingDto bookingUpdated =
                bookingService.updateBooking(ownerId, bookingSaved.getId(), false);

        assertNotNull(bookingUpdated);
        assertEquals(bookingSaved.getId(), bookingUpdated.getId());
        assertEquals(Status.REJECTED, bookingUpdated.getStatus());
        assertEquals(bookerId, bookingUpdated.getBooker().getId());
        assertEquals(itemId, bookingUpdated.getItem().getId());
        assertEquals("Гарри Поттер и Философский камень", bookingUpdated.getItem().getName());
    }

    @Test
    @Order(16)
    void getBooking() {
        long bookingId = 2L;
        OutcomingBookingDto booking = bookingService.getBooking(userId2, bookingId);

        assertNotNull(booking);
        assertEquals(bookingId, booking.getId());
        assertEquals(Status.REJECTED, booking.getStatus());
        assertNotNull(booking.getStart());
        assertNotNull(booking.getEnd());
        assertEquals(userId2, booking.getBooker().getId());
        assertEquals(1L, booking.getItem().getId());
        assertEquals("Гарри Поттер и Философский камень", booking.getItem().getName());
    }

    @Test
    @Order(17)
    void getBookings() throws InterruptedException {
        List<OutcomingBookingDto> allBookings =
                bookingService.getBookings(userId1, State.ALL, 0, 10);

        assertEquals(1, allBookings.size());
        assertEquals(1L, allBookings.get(0).getId());

        List<OutcomingBookingDto> futureBookings =
                bookingService.getBookings(userId1, State.FUTURE, 0, 10);
        assertEquals(1, futureBookings.size());
        assertEquals(1L, futureBookings.get(0).getId());

        List<OutcomingBookingDto> rejectedBookings = bookingService.getBookings(userId2,
                State.REJECTED, 0, 10);
        assertEquals(1, rejectedBookings.size());
        assertEquals(2L, rejectedBookings.get(0).getId());

        BookingDto current = new BookingDto(1L, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusDays(1));
        OutcomingBookingDto dto = bookingService.addBooking(userId2, current);

        List<OutcomingBookingDto> waitingBookings = bookingService.getBookings(
                userId2, State.WAITING, 0, 10);
        assertEquals(1, waitingBookings.size());
        assertEquals(dto.getId(), waitingBookings.get(0).getId());

        Thread.sleep(3000);

        List<OutcomingBookingDto> currentBookings =
                bookingService.getBookings(userId2, State.CURRENT, 0, 10);
        assertEquals(1, currentBookings.size());
        assertEquals(dto.getId(), currentBookings.get(0).getId());

        BookingDto past = new BookingDto(2L, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        OutcomingBookingDto dtoPast = bookingService.addBooking(userId1, past);

        Thread.sleep(3000);

        List<OutcomingBookingDto> pastBookings =
                bookingService.getBookings(userId1, State.PAST, 0, 10);
        assertEquals(1, pastBookings.size());
        assertEquals(dtoPast.getId(), pastBookings.get(0).getId());
    }

    @Test
    @Order(18)
    void getBookingsForOwnedItems() {
        List<OutcomingBookingDto> allBookings = bookingService.getBookingsForOwnedItems(userId1,
                State.ALL, 0, 2);

        assertEquals(2, allBookings.size());
        assertEquals(2L, allBookings.get(0).getId());

        List<OutcomingBookingDto> pastBookings = bookingService.getBookingsForOwnedItems(userId2,
                State.PAST, 0, 2);

        assertEquals(1, pastBookings.size());
        assertEquals(4L, pastBookings.get(0).getId());

        List<OutcomingBookingDto> futureBookings = bookingService.getBookingsForOwnedItems(userId1,
                State.FUTURE, 0, 2);

        assertEquals(1, futureBookings.size());
        assertEquals(2L, futureBookings.get(0).getId());

        List<OutcomingBookingDto> currentBookings =
                bookingService.getBookingsForOwnedItems(userId1, State.CURRENT, 0, 2);

        assertEquals(1, currentBookings.size());
        assertEquals(3L, currentBookings.get(0).getId());

        List<OutcomingBookingDto> waitingBookings =
                bookingService.getBookingsForOwnedItems(userId1, State.WAITING, 0, 2);

        assertEquals(1, waitingBookings.size());
        assertEquals(3L, waitingBookings.get(0).getId());

    }

    @Test
    @Order(19)
    void addComment() {
        bookingService.updateBooking(userId2, 4L, true);

        InCommentDto commentDto = new InCommentDto();
        commentDto.setText("Отличная книга");

        CommentDto comment = itemService.addComment(userId1, 2L, commentDto);
        assertNotNull(comment);
        assertEquals(1L, comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals("Potter", comment.getAuthorName());
        assertTrue(comment.getCreated().isBefore(LocalDateTime.now()));
    }

    @Test
    @Order(20)
    void getItemById_ForOwner() {
        long itemId = 2L;
        ItemCommentBookingDto item = itemService.getItemById(userId2, itemId);

        assertNotNull(item);
        assertEquals(itemId, item.getId());

        List<CommentDto> comments = item.getComments();
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(1L, comments.get(0).getId());
        assertNotNull(item.getNextBooking());
        assertEquals(1L, item.getNextBooking().getId());
        assertNotNull(item.getLastBooking());
        assertEquals(4L, item.getLastBooking().getId());
    }

    @Test
    @Order(21)
    void getItemById_NotOwner() {
        long itemId = 1L;
        ItemCommentBookingDto item = itemService.getItemById(userId2, itemId);

        assertNotNull(item);
        assertEquals(itemId, item.getId());

        List<CommentDto> comments = item.getComments();
        assertNotNull(comments);
        assertEquals(0, comments.size());
        assertNull(item.getNextBooking());
        assertNull(item.getLastBooking());
    }

    @Test
    @Order(22)
    void getItems_withBookingsAndComments() {
        ItemDto itemDto = itemService.addItem(userId2, itemDtoInput3);

        List<ItemCommentBookingDto> items = itemService.getItems(userId2, 0, 2);
        assertEquals(2, items.size());
        assertEquals(itemDto.getId(), items.get(1).getId());
        assertEquals(0, items.get(1).getComments().size());
        assertNull(items.get(1).getNextBooking());
        assertNull(items.get(1).getLastBooking());
        assertEquals(2L, items.get(0).getId());

        List<CommentDto> comments = items.get(0).getComments();
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(1L, comments.get(0).getId());

        ItemCommentBookingDto dto = items.get(0);

        assertNotNull(dto.getNextBooking());
        assertEquals(1L, dto.getNextBooking().getId());
        assertNotNull(dto.getLastBooking());
        assertEquals(4L, dto.getLastBooking().getId());
    }

    @Test
    @Order(23)
    void searchItems() {
        List<ItemDto> items = itemService.searchItems(userId1, "Книга", 0, 2);

        assertEquals(2, items.size());

        ItemDto dto = items.get(0);
        assertEquals(1L, dto.getId());
        assertTrue(dto.getAvailable());
        assertEquals("Гарри Поттер и Философский камень", dto.getName());
    }
}
