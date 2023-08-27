package ru.practicum.shareit.item.storage;

import org.apache.commons.lang3.ObjectUtils;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.function.Consumer;

public class ItemUtil {
    public static <T> void setIfNonNull(final Consumer<T> tConsumer, final T value) {
        if (value != null) {
            tConsumer.accept(value);
        }
    }

    public static Item test(Item item, ItemDto itemDto) {
        setIfNonNull(item::setName, itemDto.getName());
        setIfNonNull(item::setDescription, itemDto.getDescription());
        setIfNonNull(item::setAvailable, itemDto.getAvailable());

        item.setName(ObjectUtils.getIfNull(item.getName(), itemDto::getName));
        item.setDescription(ObjectUtils.getIfNull(item.getDescription(), itemDto::getDescription));
        item.setAvailable(ObjectUtils.getIfNull(item.getAvailable(), itemDto::getAvailable));
        return item;
    }
}
