package ru.job4j.cinema.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class TicketDto {
    private int id;
    private int sessionId;
    private String filmName;
    private String hallName;
    private LocalDateTime startTime;
    private int rowNumber;
    private int placeNumber;
    private int price;
    private String userFullName;

    public TicketDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TicketDto ticketDto = (TicketDto) o;
        return id == ticketDto.id
                && sessionId == ticketDto.sessionId
                && rowNumber == ticketDto.rowNumber
                && placeNumber == ticketDto.placeNumber
                && price == ticketDto.price
                && Objects.equals(filmName, ticketDto.filmName)
                && Objects.equals(hallName, ticketDto.hallName)
                && Objects.equals(startTime, ticketDto.startTime)
                && Objects.equals(userFullName, ticketDto.userFullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sessionId, filmName, hallName, startTime,
                rowNumber, placeNumber, price, userFullName);
    }
}
