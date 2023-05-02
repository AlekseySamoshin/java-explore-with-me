package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("select r from ParticipationRequest r" +
            "where r.event.initiator = :userId")
    List<ParticipationRequest> findByUserId(Long userId);
}
