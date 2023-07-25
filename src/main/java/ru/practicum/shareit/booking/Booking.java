package ru.practicum.shareit.booking;


import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Builder
public class Booking {

    @Id
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;


    private Item item;


    @ManyToOne
    private User booker;

    private Status status;

}
