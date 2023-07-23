package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto userDto) {
        return userStorage.addUser(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        userStorage.getUserById(userId);
        return userStorage.updateUser(userDto, userId);
    }

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public UserDto getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.getUserById(userId);
        userStorage.deleteUser(userId);

    }
}
