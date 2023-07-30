package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 and b.end > ?3 " +
            "order by b.start desc")
    List<Booking> findAllCurrent(Long id, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long id);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllPast(Long id, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllFuture(Long id, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItem_IdInOrderByStartDesc(List<Long> itemIds);

    @Query("select b from Booking  b " +
            "where b.item.id in ?1 " +
            "and b.start < ?2 and b.end > ?3 " +
            "order by b.start desc")
    List<Booking> findAllByItem_IdCurrent(List<Long> itemIds, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItem_IdInAndEndIsBeforeOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    Collection<Booking> findAllByItem_IdInAndStartIsAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    Collection<Booking> findAllByItemId_IdInAndStatusOrderByStartDesc(List<Long> itemIds, Status status);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = ?2 " +
            "and b.start <= ?3 " +
            "order by b.end desc")
    List<Booking> findLastBooking(long itemId, Status status, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and b.booker.id = ?2 " +
            "and b.end <= ?3 " +
            "and b.status = ?4 " +
            "group by b.id " +
            "order by b.end desc")
    List<Booking> findLastBookingByBooker(long itemId, long userId, LocalDateTime now, Status status);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = ?2 " +
            "and b.start >= ?3 " +
            "order by b.start")
    List<Booking> findNextBooking(long itemId, Status status, LocalDateTime now);
}
