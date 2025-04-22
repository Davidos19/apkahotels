package org.example.apkahotels.controllers;

import lombok.Getter;
import org.example.apkahotels.models.Reservation;
import org.example.apkahotels.services.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationCalendarController {

    private final ReservationService reservationService;

    public ReservationCalendarController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Endpoint zwracający rezerwacje w formacie przyjaznym dla kalendarza
    @GetMapping("/calendar")
    public List<ReservationEvent> getReservationEvents() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return reservations.stream()
                .map(reservation -> new ReservationEvent(
                        reservation.getId(),
                        "Pokój " + reservation.getRoomId(), // Możesz zmodyfikować tytuł, np. dodać nazwę hotelu
                        reservation.getCheckIn(),
                        reservation.getCheckOut()
                ))
                .collect(Collectors.toList());
    }
}

@Getter
class ReservationEvent {
    private final Long id;
    private final String title;
    private final String start;
    private final String end;

    public ReservationEvent(Long id, String title, LocalDate checkIn, LocalDate checkOut) {
        this.id = id;
        this.title = title;
        // Format ISO-8601, akceptowany przez FullCalendar
        this.start = checkIn.toString();
        this.end = checkOut.toString();
    }

}
