package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.CommentDto;
import ru.practicum.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c " +
            "where c.event.id = :eventId " +
            "order by c.created desc")
    List<Comment> findAllByEventId(Long eventId, Pageable page);

    @Query("select c from Comment c " +
            "where c.author.id = :userId " +
            "order by c.created desc")
    List<Comment> findAllByUserId(Long userId, Pageable page);

    @Query("select c from Comment c " +
            "where c.event.id = :eventId " +
            "and lower(event.description) like %:text% " +
            "order by c.created desc")
    List<CommentDto> findByEventIdAndText(Long eventId, String text, Pageable page);
}
