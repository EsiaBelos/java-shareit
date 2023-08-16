package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(userDto.getName() == null ? user.getName() : userDto.getName());
            if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
                user.setEmail(userDto.getEmail());
            }
            return UserMapper.toUserDto(userRepository.save(user));
        }
        throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> userList = userRepository.findAll();
        if (!userList.isEmpty()) {
            return userList.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public UserDto getUserById(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return UserMapper.toUserDto(userOptional.get());
        }
        throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
    }

    @Override
    public void deleteUser(long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }
}
