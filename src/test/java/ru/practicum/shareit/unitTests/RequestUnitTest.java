package ru.practicum.shareit.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.OutRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestUnitTest {

    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    RequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    User user;
    Item item;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(requestRepository, userRepository, itemRepository);
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
        request = ItemRequest.builder().id(1L).build();
    }

    @Test
    void addRequest_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()->
                requestService.addRequest(anyLong(), null));
    }

    @Test
    void getRequests_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()->
                requestService.getRequests(anyLong()));
    }

    @Test
    void getRequests_whenNoRequestsFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestor_IdOrderByCreatedDesc(anyLong()))
                .thenReturn(Collections.emptyList());

        List<OutRequestDto> dtos = requestService.getRequests(user.getId());
        assertNotNull(dtos);
        assertEquals(0, dtos.size());
    }

    @Test
    void getRequestById_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()->
                requestService.getRequestById(anyLong(), 1L));
    }

    @Test
    void getRequestById_whenRequestNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, ()->
                requestService.getRequestById(user.getId(), anyLong()));
    }

    @Test
    void searchAllRequests_whenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()->
                requestService.searchAllRequests(anyLong(), 0, 10));
    }

    @Test
    void searchAllRequests_whenRequestsNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestor_IdNot(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<OutRequestDto> dtos = requestService.searchAllRequests(user.getId(), 0, 10);
        assertNotNull(dtos);
        assertEquals(0, dtos.size());
    }
}