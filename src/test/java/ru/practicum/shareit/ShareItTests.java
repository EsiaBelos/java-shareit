package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {

//	private final BookingServiceImpl bookingService;
//	private final UserRepository userRepository;
//	private final ItemRepository itemRepository;

    @Test
    void contextLoads() {
    }

//	@Test
//	public void testAddBooking() {
//		User user = User.builder()
//				.email("user@user.com")
//				.name("Harry")
//				.build();
//		User savedUser = userRepository.save(user);
//		assertNotNull(savedUser);
//		Item item = Item.builder()
//				.name("Book")
//				.description("About friends")
//				.available(false)
//				.owner(savedUser)
//				.build();
//		Item savedItem = itemRepository.save(item);
//		assertNotNull(savedItem);
//		BookingDto bookingDto = new BookingDto(1,
//				LocalDateTime.of(2023, 8, 15, 12,0), LocalDateTime.of(2023, 8, 25, 12,0));
//		assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(savedUser.getId(), bookingDto));
//	}

}
