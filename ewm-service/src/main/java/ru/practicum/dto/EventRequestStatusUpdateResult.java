package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.practicum.Constants;
import ru.practicum.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;

    public EventRequestStatusUpdateResult() {
        confirmedRequests = new ArrayList<>();
        rejectedRequests = new ArrayList<>();
    }

    public void addRequest(ParticipationRequest request) {
        String created = request.getCreated().toString();//.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        Long event = request.getEvent().getId();
        Long id = request.getId();
        Long requester = request.getRequester();
        String status = request.getStatus();
        ParticipationRequestDto result = new ParticipationRequestDto(created, event, id, requester, status);
        if(status.equals("CONFIRMED")) {
            confirmedRequests.add(result);
        } else if (status.equals("REJECTED")){
            rejectedRequests.add(result);
        }
    }
}
