package ru.practicum.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
@Builder
public class ParticipationRequest {
    private Long id;
    private String created;
    private Event event;
    private Long requester;
    private String status;
}
