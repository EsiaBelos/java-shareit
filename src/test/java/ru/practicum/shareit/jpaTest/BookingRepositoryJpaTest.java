package ru.practicum.shareit.jpaTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryJpaTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        owner = User.builder().name("Матроскин").email("cat@mail.com").build();
        userRepository.save(owner);
        booker = User.builder().name("Шарик").email("dog@mail.com").build();
        userRepository.save(booker);

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .user(owner)
                .build();
        itemRepository.save(item);

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        pageable = PageRequest.of(0, 10, Sort.by("start").descending());
    }

    @Test
    void findAllCurrent() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        List<Booking> currentBookings =
                bookingRepository.findAllCurrent(booker.getId(),
                        LocalDateTime.now(), LocalDateTime.now(),
                        pageable);

        assertEquals(1, currentBookings.size());
        assertEquals(booking.getStart(), currentBookings.get(0).getStart());
        assertEquals(booking.getEnd(), currentBookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), currentBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), currentBookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), currentBookings.get(0).getStatus());
    }

    @Test
    void findAllPast() {
        booking.setStart(LocalDateTime.now().minusHours(3));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> pastBookings =
                bookingRepository.findAllPast(booker.getId(),
                        LocalDateTime.now(), pageable);

        assertEquals(1, pastBookings.size());
        assertEquals(booking.getStart(), pastBookings.get(0).getStart());
        assertEquals(booking.getEnd(), pastBookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), pastBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), pastBookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), pastBookings.get(0).getStatus());
    }

    @Test
    void findAllFuture() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking);

        List<Booking> futureBookings =
                bookingRepository.findAllFuture(booker.getId(),
                        LocalDateTime.now(), pageable);

        assertEquals(1, futureBookings.size());
        assertEquals(booking.getStart(), futureBookings.get(0).getStart());
        assertEquals(booking.getEnd(), futureBookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), futureBookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), futureBookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), futureBookings.get(0).getStatus());
    }

    @Test
    void findAllByItem_IdCurrent() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking);

        List<Booking> bookings =
                bookingRepository.findAllByItem_IdCurrent(List.of(item.getId()),
                        LocalDateTime.now(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findLastBooking() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findLastBooking(item.getId(),
                Status.APPROVED, LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findLastBookingByBooker() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findLastBookingByBooker(item.getId(),
                booker.getId(), LocalDateTime.now(), Status.APPROVED);

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findNextBooking() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findNextBooking(item.getId(),
                Status.APPROVED, LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findLastBookingList() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findLastBookingList(Set.of(item.getId()),
                Status.APPROVED.toString(), LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findNextBookingList() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findNextBookingList(Set.of(item.getId()),
                Status.APPROVED.toString(), LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}