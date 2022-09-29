package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoWithBookerAndItem {
    private long id; //уникальный идентификатор бронирования
    @Future
    private LocalDateTime start;//дата и время начала бронирования;
    @Future
    private LocalDateTime end;  //дата и время конца бронирования;
    private BookingStatus status;       //статус бронирования.
    private User booker;
    @JsonProperty("item")
    private ItemDto itemDto;
}
