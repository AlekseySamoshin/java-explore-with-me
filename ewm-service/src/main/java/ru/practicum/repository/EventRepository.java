package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByCategoryId(Long catId);

    @Query("select event from Event event where event.initiator.id in :users " +
            "and event.state in :states " +
            "and event.category.id in :categories " +
            "order by event.eventDate desc")
    Arrays findAllEventsWithoutDates(List<Long> users, List<String> states, List<Long> categories, PageRequest of);

    @Query("select event from Event event where event.initiator.id in :users " +
            "and event.state in :states " +
            "and event.category.id in :categories " +
            "and event.eventDate between :rangeStart and :rangeEnd " +
            "order by event.eventDate desc")
    List<Event> findAllEventsWithDates(List<Long> users,
                                       List<EventState> states,
                                       List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Pageable page);

    @Query("select event from Event event where event.initiator in :user")
    List<Event> findAllByUserId(User user, Pageable page);

    @Query("select event from Event event "
//            "where (lower(e.annotation) like '%?1%' " +
//            "or lower(e.description) like '%?1%') " +
//            "and e.start_date > current_timestamp " +
            /*"order by e.start_date desc"*/)
    List<Event> findEventsByText(String text, Pageable page);

    @Query("select event from Event event " +
            "where (lower(event.annotation) like %:text% " +
            "or lower(event.description) like %:text%) " +
            "and event.eventDate >= :startDate " +
            "and event.eventDate <= :endDate " +
            "order by event.eventDate desc")
    List<Event> findAllByTextAndDateRange(String text, LocalDateTime startDate, LocalDateTime endDate, Pageable page);
}
