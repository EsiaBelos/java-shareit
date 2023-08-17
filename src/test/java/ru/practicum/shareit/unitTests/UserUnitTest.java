package ru.practicum.shareit.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserUnitTest {

    private UserServiceImpl userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getUsers_whenEmpty() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<UserDto> users = userService.getUsers();
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void deleteUser_whenUserNotFound() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(anyLong()));
        verify(userRepository, atMostOnce()).delete(new User());
    }

    @Test
    public void getUserById_whenUserNotFound() {

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(anyLong()));
        verify(userRepository, atMostOnce()).findById(anyLong());
    }

    @Test
    public void updateUser_whenUserNotFound() {

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(null, 1L));
        verify(userRepository, never()).save(new User());
    }

}
