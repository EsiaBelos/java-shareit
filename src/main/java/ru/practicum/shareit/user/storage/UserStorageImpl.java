package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        isAvailableEmail(user.getEmail());
        user.setId(getId());
        users.put(user.getId(), user);
        log.debug("Создан пользователь с id {} именем {}", user.getId(), user.getName());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = users.get(userId);
        user.setName(userDto.getName() == null ? user.getName() : userDto.getName());
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            isAvailableEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        users.put(user.getId(), user);
        log.debug("Обновлен пользователь с id {} именем {}", user.getId(), user.getName());
        return UserMapper.toUserDto(user);
    }


    @Override
    public UserDto getUserById(long userId) {
        if (users.containsKey(userId)) {
            return UserMapper.toUserDto(users.get(userId));
        }
        throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
    }

    @Override
    public List<UserDto> getUsers() {
        if (!users.values().isEmpty()) {
            return users.values().stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
        log.debug("Удален пользователь с id {}", userId);
    }

    private Long getId() {
        return ++id;
    }

    private void isAvailableEmail(String email) {
        boolean isDouble = users.values().stream()
                .anyMatch(userInStore -> userInStore.getEmail().equals(email));
        if (isDouble) {
            throw new EmailException(String.format("Email %s уже используется", email));
        }
    }
}
