package org.example.apkahotels.repositories;

import org.example.apkahotels.models.Reservation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReservationRepository {
    private List<Reservation> reservations = new ArrayList<>();
    private Long currentId = 1L;


    public List<Reservation> getAllReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        reservation.setId(currentId++);
        reservations.add(reservation);
    }
    public Reservation getReservationById(Long id) {
        return reservations.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void removeReservation(Long id) {
        reservations.removeIf(r -> r.getId().equals(id));
    }
    public List<Reservation> findByUsername(String username) {
        return reservations.stream()
                .filter(r -> r.getUsername() != null && r.getUsername().equals(username))
                .collect(Collectors.toList());
    }
    public boolean isRoomAvailable(Reservation newReservation) {
        return reservations.stream()
                // filtrowanie po tym samym pokoju – tutaj zakładamy, że metoda getRoom() zwraca identyfikator pokoju lub obiekt, który ma poprawnie zaimplementowany equals()
                .filter(existing -> existing.getRoom().equals(newReservation.getRoom()))
                // sprawdzenie, czy przedziały czasowe nakładają się
                .noneMatch(existing ->
                        newReservation.getStartDate().isBefore(existing.getEndDate()) &&
                                newReservation.getEndDate().isAfter(existing.getStartDate())
                );
    }

}