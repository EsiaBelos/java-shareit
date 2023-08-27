package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.OutRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public OutRequestDto addRequest(@RequestBody ItemRequestDto requestDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.addRequest(userId, requestDto);
    }

    //    получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<OutRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestsByRequestor(userId);
    }

    // Посмотреть данные об отдельном запросе может любой пользователь.
    @GetMapping("/{requestId}")
    public OutRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<OutRequestDto> searchAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }
}
