package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidOwnerException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemUtil;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        log.debug("Item created id {}", item.getId());
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        Item itemInstore = getItemById(userId, itemId);
        if (itemInstore.getOwner().getId() == userId) {
            Item itemUpdated = ItemUtil.test(itemInstore, itemDto);
            return ItemMapper.toItemDto(repository.save(itemUpdated));
        }
        throw new InvalidOwnerException(String.format("Пользователь id = %d не владеет вещью с id %d", userId, itemId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        User owner = checkUser(userId);
        List<Item> items = repository.findAllByOwner(owner);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long userId, long itemId) {
        checkUser(userId);
        Optional<Item> item = repository.findById(itemId);
        if (item.isPresent()) {
            return item.get();
        }
        throw new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId));
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        checkUser(userId);
        List<Item> items = repository.searchItems(text);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User checkUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException(String.format("Пользователь с id %d не найден", userId));
    }
}
