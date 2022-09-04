package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * запрос для бронирования
 */
@Data
@Builder
public class ItemRequest {
    private long id;            // уникальный идентификатор запроса
    private String description; // текст запроса, содержащий описание требуемой вещи
    private User requestor;     // пользователь, создавший запрос
    private Date created;       // дата и время создания запроса

}
