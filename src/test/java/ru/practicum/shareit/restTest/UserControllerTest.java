package ru.practicum.shareit.restTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private final UserDto userDto = UserDto.builder().id(1L).email("user@users.com").name("Kate")
            .build();


    @Test
    @SneakyThrows
    void addUser() {
        doReturn(userDto)
                .when(userService)
                .addUser(any(UserDto.class));

        mockMvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @SneakyThrows
    void addUser_whenUserNotValid() {
        UserDto invalidDto = UserDto.builder().build();

        mockMvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(UserDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    void updateUser() {
        doReturn(userDto)
                .when(userService)
                .updateUser(any(UserDto.class), anyLong());

        mockMvc.perform(patch("/users/{id}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        doThrow(UserNotFoundException.class)
                .when(userService)
                .updateUser(any(UserDto.class), anyLong());

        mockMvc.perform(patch("/users/{userId}", 0)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getUsers() {
        doReturn(List.of(userDto))
                .when(userService)
                .getUsers();

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())));
    }

    @Test
    @SneakyThrows
    void getUserById() {
        doReturn(userDto)
                .when(userService)
                .getUserById(anyLong());

        mockMvc.perform(get("/users/{userId}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        doThrow(new UserNotFoundException("пользователь не найден"))
                .when(userService)
                .getUserById(anyLong());

        mockMvc.perform(get("/users/{userId}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        mockMvc.perform(delete("/users/{userId}", 1)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());

        doThrow(UserNotFoundException.class)
                .when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{userId}", 1)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isNotFound());
    }
}