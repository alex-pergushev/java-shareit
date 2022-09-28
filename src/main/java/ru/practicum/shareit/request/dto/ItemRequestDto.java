package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Date;

/**
 * отображение для запроса вещи
 */
@Data
@Builder
public class ItemRequestDto {

    private Long id;            // уникальный идентификатор запроса
    private String description; // текст запроса, содержащий описание требуемой вещи
    private UserDto requestor;     // пользователь, создавший запрос
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date created;       // дата и время создания запроса
}
