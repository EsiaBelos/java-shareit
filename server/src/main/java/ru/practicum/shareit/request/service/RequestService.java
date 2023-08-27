package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.OutRequestDto;

import java.util.List;

public interface RequestService {
    OutRequestDto addRequest(long userId, ItemRequestDto requestDto);

    List<OutRequestDto> getRequestsByRequestor(long userId);

    OutRequestDto getRequestById(long userId, long requestId);

    List<OutRequestDto> getAllRequests(long userId, Integer from, Integer size);
}
