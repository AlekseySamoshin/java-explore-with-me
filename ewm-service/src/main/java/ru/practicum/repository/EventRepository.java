package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByCategoryId(Long catId);

    @Query("select e from Event e where e.initiator in :users" +
            "and e.state in :states" +
            "and e.categories in :categories" +
            "and e.eventDate between :rangeStart and :rangeEnd" +
            "order by e.eventDate desc")
    List<Event> findAllEvents(List<Long> users,
                              List<String> states,
                              List<Long> categories,
                              LocalDateTime rangeStart,
                              LocalDateTime rangeEnd,
                              Pageable page);

    List<Event> findAllByUserId(User user, Pageable page);

    @Query("select e from Event e " +
            "where (lower(e.annotation) like '%:text%' " +
            "or lower(e.description) like '%text%') " +
            "and e.startDate >= current_timestamp " +
            "order by e.startDate desc")
    List<Event> findEventsByText(String lowerCase, Pageable page);

    @Query("select e from Event e " +
            "where (lower(e.annotation) like '%:text%' " +
            "or lower(e.description) like '%text%') " +
            "and e.startDate >= :startDate " +
            "and e.endDate <= :endDate " +
            "order by e.startDate desc")
    List<Event> findAllByTextAndDateRange(String text, LocalDateTime startDate, LocalDateTime endDate, Pageable page);
}
