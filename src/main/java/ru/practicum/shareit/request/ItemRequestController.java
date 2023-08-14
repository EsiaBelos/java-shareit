package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.OutRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public OutRequestDto addRequest(@Valid @RequestBody ItemRequestDto requestDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.addRequest(userId, requestDto);
    }

//    получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<OutRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestsByRequestor(userId);
    }

    // Посмотреть данные об отдельном запросе может любой пользователь.
    @GetMapping("/{requestId}")
    public OutRequestDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<OutRequestDto> searchAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }
}
