package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.OutRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    OutRequestDto addRequest(long userId, ItemRequestDto requestDto);

    List<OutRequestDto> getRequests(long userId);

    OutRequestDto getRequestById(long userId, long requestId);

    List<OutRequestDto> searchAllRequests(long userId, Integer from, Integer size);
}
