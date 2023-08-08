package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where lower(i.name) like lower(concat( '%', ?1, '%')) " +
            "or lower(i.description) like lower(concat( '%', ?1, '%'))")
    List<Item> searchItems(String text);

    List<Item> findAllByUserIdOrderById(long userId);

    @Query("select i.id from Item i " +
            "where i.user.id = ?1")
    List<Long> findItemIds(long userId);

    Set<Item> findAllByRequest_Id(long requestId);

    List<Item> findAllByRequest_IdIn(List<Long> requestIds);
}
