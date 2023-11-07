package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody ItemRequestDto requestDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.addRequest(userId, requestDto);
    }

    //    получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getRequestsByRequestor(userId);
    }

    // Посмотреть данные об отдельном запросе может любой пользователь.
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> searchAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                    @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return requestClient.getAllRequests(userId, from, size);
    }
}
