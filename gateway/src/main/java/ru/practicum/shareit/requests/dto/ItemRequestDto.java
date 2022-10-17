package ru.practicum.shareit.requests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    @JsonProperty("id")
    private long id;
    @NotNull
    private String description;     //текст запроса, содержащий описание требуемой вещи;
    private long requestor;         //пользователь, создавший запрос;
    private LocalDateTime created;  //дата и время создания запроса.
    private List<ItemDto> items;
}
