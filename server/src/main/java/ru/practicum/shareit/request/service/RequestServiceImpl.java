package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.OutRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public OutRequestDto addRequest(long userId, ItemRequestDto requestDto) {
        User user = checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, user);
        return ItemRequestMapper.toOutRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<OutRequestDto> getRequestsByRequestor(long userId) { //от более новых к более старым
        checkUser(userId);
        List<ItemRequest> requestList = requestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        if (requestList.isEmpty()) {
            return Collections.emptyList();
        }
        return getRequestsWithItems(requestList);
    }

    @Override
    public OutRequestDto getRequestById(long userId, long requestId) {
        checkUser(userId);
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new RequestNotFoundException(String.format("Запрос id = %d не найден", requestId)));
        OutRequestDto dto = ItemRequestMapper.toOutRequestDto(request);
        Set<Item> items = itemRepository.findAllByRequest_Id(requestId);
        dto.getItems().addAll(items.stream().map(ItemMapper::toItemDto).collect(Collectors.toSet()));
        return dto;
    }

    @Override
    public List<OutRequestDto> getAllRequests(long userId, Integer from, Integer size) {
        checkUser(userId);
        Pageable sortedByCreated = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        List<ItemRequest> requests = requestRepository.findAllByRequestor_IdNot(userId, sortedByCreated);
        return getRequestsWithItems(requests);
    }

    private User checkUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return user;
    }

    private List<OutRequestDto> getRequestsWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> itemList = itemRepository.findAllByRequest_IdIn(requestIds);

        List<OutRequestDto> requestDtos = requests.stream()
                .map(ItemRequestMapper::toOutRequestDto)
                .collect(Collectors.toList());
        if (itemList.isEmpty()) {
            return requestDtos;
        }

        List<ItemDto> itemDtoList = itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        Map<Long, List<ItemDto>> itemMap = itemDtoList.stream().collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requestDtos.stream()
                .peek(request -> {
                    if (itemMap.containsKey(request.getId())) {
                        request.getItems().addAll(itemMap.get(request.getId()));
                    }
                })
                .collect(Collectors.toList());
    }
}
