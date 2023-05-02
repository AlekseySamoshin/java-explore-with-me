package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateResult {
    List<ParticipationRequest> confirmedRequests;
    List<ParticipationRequest> rejectedRequests;
}
