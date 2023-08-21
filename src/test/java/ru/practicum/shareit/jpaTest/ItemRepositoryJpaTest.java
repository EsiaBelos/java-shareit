package ru.practicum.shareit.jpaTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryJpaTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = User.builder().name("Матроскин").email("cat@mail.com").build();
        userRepository.save(owner);

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .user(owner)
                .build();
        itemRepository.save(item);
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void searchItems() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.searchItems("name", pageable);

        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());

        List<Item> items2 = itemRepository.searchItems("NaMe", pageable);

        assertEquals(1, items2.size());
        assertEquals(item.getId(), items2.get(0).getId());
    }

    @Test
    void findItemIds() {
        List<Long> itemIds = itemRepository.findItemIds(owner.getId());

        assertEquals(1, itemIds.size());
        assertEquals(item.getId(), itemIds.get(0));
    }
}