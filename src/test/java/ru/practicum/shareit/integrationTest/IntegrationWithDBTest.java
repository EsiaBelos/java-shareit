package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
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
    ItemRequestDto itemRequestDto1 = new ItemRequestDto().setDescription("Книга про Винни Пуха");
    ItemRequestDto itemRequestDto2 = new ItemRequestDto().setDescription("Самокат десткий");

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
        User user = userService.addUser(dtoInput); //ADD

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

        User updatedUser_WithName = userService.updateUser(updateUserDto_WithName, user.getId());
        assertNotNull(updatedUser_WithName);
        assertEquals(1L, updatedUser_WithName.getId());
        assertEquals(updateUserDto_WithName.getName(), updatedUser_WithName.getName());
        assertEquals(user.getEmail(), updatedUser_WithName.getEmail());

        UserDto updateUserDto_WithEmail = UserDto.builder()
                .email("harry@user.com")
                .build();

        User updatedUser_WithEmail = userService.updateUser(updateUserDto_WithEmail, user.getId());
        assertNotNull(updatedUser_WithEmail);
        assertEquals(1L, updatedUser_WithEmail.getId());
        assertEquals(updatedUser_WithName.getName(), updatedUser_WithEmail.getName());
        assertEquals(updateUserDto_WithEmail.getEmail(), updatedUser_WithEmail.getEmail());

        UserDto fullDto = UserDto.builder()
                .name("Potter")
                .email("harry@potter.com")
                .build();
        User updatedUser_Full = userService.updateUser(fullDto, user.getId());
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
        User user = userService.addUser(dto);
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
        itemDtoInput1.setAvailable(false);
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
        long userId = 1L;
        OutRequestDto dto = requestService.addRequest(userId, itemRequestDto2);
        List<OutRequestDto> requests = requestService.getRequestsByRequestor(userId);

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
        long userId = 3L;

        itemDtoInput2.setRequestId(requestId);
        ItemDto savedItem = itemService.addItem(userId, itemDtoInput2);

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
        long userId = 1L;
        itemDtoInput2.setAvailable(true);
        itemDtoInput2.setRequestId(2L);

        ItemDto updatedItem = itemService.updateItem(3L, itemDtoInput2, itemId);

        assertNotNull(updatedItem);
        assertEquals(itemId, updatedItem.getId());
        assertEquals(itemDtoInput2.getName(), updatedItem.getName());
        assertEquals(itemDtoInput2.getDescription(), updatedItem.getDescription());
        assertEquals(itemDtoInput2.getAvailable(), updatedItem.getAvailable());
        assertEquals(2L, updatedItem.getRequestId());

        List<OutRequestDto> requests = requestService.getRequestsByRequestor(userId);

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
        long userId = 1L;
        long requestId = 2L;
        OutRequestDto outRequestDto = requestService.getRequestById(userId, requestId);

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
        List<OutRequestDto> requestDtos = requestService.getAllRequests(3L, 0, 2);

        assertNotNull(requestDtos);
        assertEquals(2, requestDtos.size());

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

}
