package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.EventShortDto;
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
}
