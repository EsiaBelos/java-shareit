package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public OutcomingBookingDto.BookerDto toBookerDto(User user) {
        return new OutcomingBookingDto.BookerDto(user.getId());
    }
}
