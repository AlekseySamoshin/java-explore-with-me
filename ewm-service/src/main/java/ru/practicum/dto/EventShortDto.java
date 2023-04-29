package ru.practicum.dto;

public class EventShortDto {
    String annotation;
//    example: Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории
//    Краткое описание

    CategoryDto category;
//    confirmedRequests	integer($int64)
//    example: 5
//    Количество одобренных заявок на участие в данном событии

    String eventDate;
//    example: 2024-12-31 15:10:05
//    Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    String id;
//    example: 1
//    Идентификатор

    UserShortDto initiator;

    Boolean paid;
//    example: true
//    Нужно ли оплачивать участие

    String title;
//    example: Знаменитое шоу 'Летающая кукуруза'
//    Заголовок

    Long views;
//    example: 999
//    Количество просмотрев события
}
