package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();

    private Long id = 0L;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(getId());
        if (!items.containsKey(userId)) {
            items.put(userId, new ArrayList<>());
        }
        items.compute(userId, (user, userItems) -> {
            userItems.add(item);
            return userItems;
        });
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        if (items.containsKey(userId)) {
            Item itemInStore = items.get(userId).stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .get();
            Item itemUpdated = ItemUtil.test(itemInStore, itemDto);
            items.compute(userId, (user, userItems) -> {
                userItems.remove(itemInStore);
                userItems.add(itemUpdated);
                return userItems;
            });
            return ItemMapper.toItemDto(itemUpdated);
        }
        throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return null;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Optional<ItemDto> itemDto = Optional.empty();
        for (List<Item> itemsInStore : items.values()) {
            itemDto = itemsInStore.stream()
                    .filter(item -> item.getId().equals(itemId))
                    .map(ItemMapper::toItemDto)
                    .findFirst();
        }
        if (itemDto.isPresent()) {
            return itemDto.get();
        }
        throw new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId));
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        return null;
    }

    private Long getId() {
        return ++id;
    }
}
