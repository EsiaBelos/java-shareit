package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserById(long userId);

    List<UserDto> getUsers();

    void deleteUser(long userId);
}
