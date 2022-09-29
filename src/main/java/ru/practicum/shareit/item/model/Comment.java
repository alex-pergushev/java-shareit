package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;        //уникальный идентификатор комментария
    private String text;    //содержимое комментария

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;      //вещь, к которой относится комментарий

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;    //автор комментария

    private LocalDateTime created;   //дата создания комментария

}
