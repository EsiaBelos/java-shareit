package ru.practicum.shareit.restTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InCommentDto;
import ru.practicum.shareit.item.dto.ItemCommentBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.dto.ItemCommentBookingDto.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();
    private ItemCommentBookingDto dto = builder()
            .id(1L)
            .name("item")
            .description("withBookingsAndComments")
            .available(true)
            .lastBooking(new ItemBooking(1L, 1L))
            .nextBooking(new ItemBooking(2L, 2L))
            .build();
    private CommentDto commentDto = new CommentDto(1L, "comment", LocalDateTime.now(),
            "user");

    @Test
    @SneakyThrows
    void addItem() {
        doReturn(itemDto)
                .when(itemService)
                .addItem(anyLong(), any(ItemDto.class));

        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        itemDto.setName("");
        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void addComment() {
        InCommentDto dto = new InCommentDto();

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        dto.setText("comment");

        doReturn(commentDto)
                .when(itemService)
                .addComment(anyLong(), anyLong(), any(InCommentDto.class));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        doThrow(IllegalArgumentException.class)
                .when(itemService)
                .addComment(anyLong(), anyLong(), any(InCommentDto.class));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateItem() {
        doThrow(new AccessDeniedException(""))
                .when(itemService)
                .updateItem(anyLong(), any(ItemDto.class), anyLong());
        mvc.perform(patch("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());

        doReturn(itemDto)
                .when(itemService)
                .updateItem(anyLong(), any(ItemDto.class), anyLong());

        mvc.perform(patch("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    void getItems() {
        doReturn(List.of(dto))
                .when(itemService)
                .getItems(anyLong(), anyInt(), anyInt());

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.[0].nextBooking.id",
                        is(dto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId",
                        is(dto.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.id",
                        is(dto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId",
                        is(dto.getLastBooking().getBookerId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getItemById() {
        doReturn(dto)
                .when(itemService)
                .getItemById(anyLong(), anyLong());

        mvc.perform(get("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.nextBooking.id",
                        is(dto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId",
                        is(dto.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.id",
                        is(dto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId",
                        is(dto.getLastBooking().getBookerId()), Long.class));


        doThrow(ItemNotFoundException.class)
                .when(itemService)
                .getItemById(anyLong(), anyLong());

        mvc.perform(get("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    @SneakyThrows
    void searchItems() {
        doReturn(List.of(itemDto))
                .when(itemService)
                .searchItems(anyLong(), anyString(), anyInt(), anyInt());

        mvc.perform(get("/items/search")
                        .param("text", "name")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }
}