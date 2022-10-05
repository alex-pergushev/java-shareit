package ru.practicum.shareit.datajpatests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@DataJpaTest
public class BookingTests {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager em;

    private static Item item;
    private static User userOwner;
    private static User userBooker;
    private static Booking booking;

    private static final LocalDateTime start = LocalDateTime.now();
    private static final LocalDateTime end = LocalDateTime.now().plusHours(3);

    @BeforeEach
    public void setUp() {
        item = Item.builder()
                .name("Дрель")
                .description("С ударным механизмом")
                .owner(1)
                .isAvailable(true)
                .request(0)
                .build();

        userOwner = User.builder()
                .name("Alex")
                .email("alex@pergushev.ru")
                .build();

        userBooker = User.builder()
                .name("Kirill")
                .email("kirill@pergushev.ru")
                .build();

        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(userBooker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveItem() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);
        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(booking.getStart(), start);
        Assertions.assertEquals(booking.getStatus(), BookingStatus.WAITING);
    }

    @Test
    void findAllByBookerId() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBookerId(userBooker.getId(), Pageable.unpaged());
        Assertions.assertNotNull(bookings);
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBookerIdAndEndBefore(userBooker.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByBookerIdAndEndBefore(userBooker.getId(), LocalDateTime.now().plusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBookerIdAndStartAfter(userBooker.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByBookerIdAndStartAfter(userBooker.getId(), LocalDateTime.now().minusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBookerIdAndStartBeforeAndEndAfter(userBooker.getId(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBookerIdAndStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBookerIdAndStatus(userBooker.getId(), BookingStatus.REJECTED, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByBookerIdAndStatus(userBooker.getId(), BookingStatus.WAITING, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItemIdIn() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItemIdIn(userOwner.getId(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);

        bookings = repository.findAllByItemIdIn(userBooker.getId(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);
    }

    @Test
    void findAllByItemIdInAndEndBefore() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItemIdInAndEndBefore(userOwner.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByItemIdInAndEndBefore(userOwner.getId(), LocalDateTime.now().plusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItemIdInAndStartAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItemIdInAndStartAfter(userOwner.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByItemIdInAndStartAfter(userOwner.getId(), LocalDateTime.now().minusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItemIdInAndStartBeforeAndEndAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItemIdInAndStartBeforeAndEndAfter(userOwner.getId(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItemIdInAndStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItemIdInAndStatus(userOwner.getId(), BookingStatus.REJECTED, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByItemIdInAndStatus(userOwner.getId(), BookingStatus.WAITING, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void getByIdForResponse() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Comment comment = Comment.builder()
                .text("Отзыв")
                .item(item)
                .created(LocalDateTime.now())
                .author(userBooker)
                .build();
        commentRepository.save(comment);

        ItemDto itemDto = itemRepository.getByIdForResponse(userBooker.getId(), item.getId());
        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(itemDto.getComments().size(), 1);
    }


}
