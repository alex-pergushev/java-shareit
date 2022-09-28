package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Date;

/**
 * запрос для бронирования
 */
@Entity
@Table(name = "item_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            // уникальный идентификатор запроса

    private String description; // текст запроса, содержащий описание требуемой вещи

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;     // пользователь, создавший запрос
    private Date created;       // дата и время создания запроса

}
