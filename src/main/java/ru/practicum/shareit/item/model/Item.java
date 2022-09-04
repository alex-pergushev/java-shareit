package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotNull;

/**
 * вещь для аренды
 */
@Data
@Builder
public class Item {
    @NotNull
    private long id;                //уникальный идентификатор вещи
    private String name;            //краткое название
    private String description;     //развёрнутое описание
    private boolean available;      //статус о том, доступна или нет вещь для аренды;
    private User owner;             //владелец вещи
    private ItemRequest request;    //если вещь была создана по запросу другого пользователя,
                                    // то в этом поле будет храниться ссылка на соответствующий запрос.

}
