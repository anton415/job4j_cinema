package ru.job4j.cinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketMapper {

    @Mapping(target = "id", source = "ticket.id")
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "filmName", source = "film.name")
    @Mapping(target = "hallName", source = "hall.name")
    @Mapping(target = "startTime", source = "session.startTime")
    @Mapping(target = "rowNumber", source = "ticket.rowNumber")
    @Mapping(target = "placeNumber", source = "ticket.placeNumber")
    @Mapping(target = "price", source = "session.price")
    @Mapping(target = "userFullName", source = "user.fullName")
    TicketDto toDto(Ticket ticket, FilmSession session, Film film, Hall hall, User user);
}
