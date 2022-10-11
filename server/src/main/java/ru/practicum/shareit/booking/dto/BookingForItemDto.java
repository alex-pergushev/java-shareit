package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingForItemDto {
    private long id;
    private long bookerId;

    public BookingForItemDto(long id, long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
