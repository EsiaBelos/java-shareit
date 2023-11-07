package ru.practicum.shareit.restTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.OutRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    RequestService requestService;
    private static ItemRequestDto itemRequestDto;
    private static OutRequestDto outRequestDto;

    @BeforeAll
    static void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("баскетбольный мяч");

        outRequestDto = OutRequestDto.builder()
                .id(1L)
                .description("баскетбольный мяч")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @SneakyThrows
    void addRequest() {
        doReturn(outRequestDto)
                .when(requestService)
                .addRequest(anyLong(), any(ItemRequestDto.class));

        mvc.perform(post("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(outRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(outRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

    }

    @Test
    @SneakyThrows
    void getRequests() {
        doReturn(List.of(outRequestDto))
                .when(requestService)
                .getRequestsByRequestor(anyLong());

        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(outRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description",
                        is(outRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].created",
                        is(outRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        doReturn(outRequestDto)
                .when(requestService)
                .getRequestById(anyLong(), anyLong());

        mvc.perform(get("/requests/{requestId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(outRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(outRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void searchAllRequests() {
        doReturn(List.of(outRequestDto))
                .when(requestService)
                .getAllRequests(anyLong(), anyInt(), anyInt());

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(outRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description",
                        is(outRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].created",
                        is(outRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void searchAllItems_whenInvalidParams() {
        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1"))
                .andExpect(status().isInternalServerError());

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("size", "-1"))
                .andExpect(status().isInternalServerError());
    }
}