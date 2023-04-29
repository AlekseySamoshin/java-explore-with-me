package ru.practicum.dto;

import java.util.List;

public class EventRequestStatusUpdateRequest {
    List<Long> requestIds;
    String status;
//    Enum:
//            [ CONFIRMED, REJECTED ]
}
