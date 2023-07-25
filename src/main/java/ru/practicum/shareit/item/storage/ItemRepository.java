package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where lower(i.name) like lower(concat( '%', ?1, '%')) " +
            "or lower(i.description) like lower(concat( '%', ?1, '%'))")
    List<Item> searchItems(String text);

    @Query("select i from Item i " +
            "where i.owner = ?1")
    List<Item> findAllByOwner(User owner);
}
