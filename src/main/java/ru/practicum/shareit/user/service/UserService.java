package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User addUser(UserDto userDto);

    User updateUser(UserDto userDto, Long userId);

    List<UserDto> getUsers();

    UserDto getUserById(long userId);

    void deleteUser(long userId);
}
