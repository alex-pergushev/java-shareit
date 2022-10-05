package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * // представление для вещи
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;                //уникальный идентификатор вещи
    @NotBlank(message = "Не указано наименование вещи для аренды")
    private String name;        //краткое название;
    @NotBlank(message = "Не указано описание вещи для аренды")
    private String description; //развёрнутое описание;
    @NotNull
    private Boolean available;      //статус о том, доступна или нет вещь для аренды;
    private long owner;          //владелец вещи
    @JsonProperty("requestId")
    private long request; //если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос.
    private List<CommentDtoResponse> comments;
    private BookingForItemDto lastBooking; //текущее бронирование
    private BookingForItemDto nextBooking; //следующее бронирование
}
