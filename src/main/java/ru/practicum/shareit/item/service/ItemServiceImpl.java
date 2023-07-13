package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        userStorage.getUserById(userId);
        return itemStorage.updateItem(userId, itemDto, itemId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return null;
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        userStorage.getUserById(userId);
        return itemStorage.getItemById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        return null;
    }
}
