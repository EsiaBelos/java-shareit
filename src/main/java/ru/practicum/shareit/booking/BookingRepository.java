package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 and b.end > ?3")
    List<Booking> findAllCurrent(Long id, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    List<Booking> findAllByBooker_Id(Long id, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllPast(Long id, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllFuture(Long id, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItem_IdIn(List<Long> itemIds, Pageable pageable);

    @Query("select b from Booking  b " +
            "where b.item.id in ?1 " +
            "and b.start < ?2 and b.end > ?2 ")
    List<Booking> findAllByItem_IdCurrent(List<Long> itemIds, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItem_IdInAndEndIsBefore(List<Long> itemIds, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItem_IdInAndStartIsAfter(List<Long> itemIds, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemId_IdInAndStatus(List<Long> itemIds, Status status, Pageable pageable);

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

    @Query(value = "WITH cte AS (SELECT *, " +
            "ROW_NUMBER() over (partition by item_id order by start_date desc) as rn " +
            "FROM public.bookings b " +
            "where b.item_id in :items " +
            "and b.status = :status " +
            "and b.start_date <= :date) " +
            "SELECT  * FROM cte WHERE rn = 1;", nativeQuery = true)
    List<Booking> findLastBookingList(@Param("items") Set<Long> items,
                                      @Param("status") String status,
                                      @Param("date") LocalDateTime now);

    @Query(value = "WITH cte AS (SELECT *, " +
            "ROW_NUMBER() over (partition by item_id order by start_date) as rn " +
            "FROM public.bookings b " +
            "where b.item_id in :items " +
            "and b.status = :status " +
            "and b.start_date >= :date) " +
            "SELECT  * FROM cte WHERE rn = 1;", nativeQuery = true)
    List<Booking> findNextBookingList(@Param("items") Set<Long> items,
                                      @Param("status") String status,
                                      @Param("date") LocalDateTime now);
}
