package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new LinkedHashMap<>();
    private final Map<Long, Item> storage = new LinkedHashMap<>();
    private Long id = 0L;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(getId());
        storage.put(item.getId(), item);
        if (!items.containsKey(userId)) {
            items.put(userId, new ArrayList<>());
        }
        items.compute(userId, (user, userItems) -> {
            userItems.add(item);
            return userItems;
        });
        log.debug("Item created id {}", item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        if (storage.containsKey(itemId) && items.containsKey(userId)) {
            Item itemInStore = storage.get(itemId);
            Item itemUpdated = ItemUtil.test(itemInStore, itemDto);
            storage.put(itemId, itemUpdated);
            items.compute(userId, (user, userItems) -> {
                if (userItems.contains(itemInStore)) {
                    userItems.remove(itemInStore);
                    userItems.add(itemUpdated);
                }
                return userItems;
            });
            log.debug("Item updated id {}", itemId);
            return ItemMapper.toItemDto(itemUpdated);
        }
        throw new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        if (items.containsKey(userId)) {
            List<Item> itemsInStore = items.get(userId);
            if (!itemsInStore.isEmpty()) {
                List<ItemDto> userItems = itemsInStore.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
                return userItems;
            }
            return Collections.emptyList();
        }
        throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (storage.containsKey(itemId)) {
            return ItemMapper.toItemDto(storage.get(itemId));
        }
        throw new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return storage.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    private Long getId() {
        return ++id;
    }
}
