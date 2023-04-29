package ru.practicum.dto;

public class ParticipationRequestDto {
    String created;
//    example: 2022-09-06T21:10:05.432
//    Дата и время создания заявки

    Long event;
//    example: 1
//    Идентификатор события

    Long id;
//    example: 3
//    Идентификатор заявки

    Long requester;
//    example: 2
//    Идентификатор пользователя, отправившего заявку

    String status;
//    example: PENDING
//    Статус заявки
}
