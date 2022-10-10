package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                //уникальный идентификатор запроса;
    @Column
    private String description;     //текст запроса, содержащий описание требуемой вещи;
    @Column(name = "requestor_id")
    private long requestor;         //пользователь, создавший запрос;
    private LocalDateTime created;  //дата и время создания запроса.
}
