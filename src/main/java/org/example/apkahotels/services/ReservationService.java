package org.example.apkahotels.services;

import org.example.apkahotels.exceptions.HotelNotAvailableException;
import org.example.apkahotels.exceptions.InvalidReservationDateException;
import org.example.apkahotels.models.Hotel;
import org.example.apkahotels.models.Reservation;
import org.example.apkahotels.repositories.HotelRepository;
import org.example.apkahotels.repositories.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    // Możesz usunąć lokalną listę, jeśli korzystasz z repository:
    // private List<Reservation> reservations = new ArrayList<>();

    private final HotelRepository hotelRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    // Konstruktor bez wstrzykiwania samego siebie
    public ReservationService(HotelRepository hotelRepository, ReservationRepository reservationRepository, UserService userService) {
        this.hotelRepository = hotelRepository;
        this.reservationRepository = reservationRepository;
        this.userService = userService;

    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.getAllHotels();
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.getHotelById(id);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.getReservationById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.getAllReservations();
    }

    public List<Hotel> searchHotels(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return hotelRepository.getAllHotels();
        }
        String lowerKeyword = keyword.toLowerCase();
        return hotelRepository.getAllHotels().stream()
                .filter(hotel -> hotel.getName().toLowerCase().contains(lowerKeyword)
                        || hotel.getLocation().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.getReservationById(id);
        if (reservation != null) {
            Hotel hotel = hotelRepository.getHotelById(reservation.getHotelId());
            if (hotel != null) {
                hotel.setAvailableRooms(hotel.getAvailableRooms() + 1);
            }
            reservationRepository.removeReservation(id);
        } else {
            throw new RuntimeException("Rezerwacja o ID: " + id + " nie została znaleziona!");
        }
    }


    public void makeReservation(Reservation reservation) {
        logger.info("Próba utworzenia rezerwacji dla hotelu id: {}", reservation.getHotelId());
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (reservation.getUsername() == null || reservation.getUsername().trim().isEmpty()) {
                reservation.setUsername(userService.getCurrentUser().getUsername());
            }
        } catch (Exception ex) {
            logger.error("Błąd przy tworzeniu rezerwacji: {}", ex.getMessage());
            throw ex;
        }

        if (reservation.getCheckIn().isAfter(reservation.getCheckOut())) {
            throw new InvalidReservationDateException("Data przyjazdu musi być przed datą wyjazdu!");
        }
        if (reservation.getCheckIn().isBefore(LocalDate.now())) {
            throw new InvalidReservationDateException("Data przyjazdu nie może być w przeszłości!");
        }

        // Nowa weryfikacja dostępności pokoju pod kątem kolidujących rezerwacji
        if (!isRoomAvailable(reservation)) {
            throw new RuntimeException("Wybrany pokój jest już zarezerwowany w wybranym terminie!");
        }

        Hotel hotel = hotelRepository.getHotelById(reservation.getHotelId());
        if (hotel != null && hotel.getAvailableRooms() > 0) {
            hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
            reservationRepository.addReservation(reservation);
        } else {
            throw new HotelNotAvailableException("Brak dostępnych pokoi!");
        }
    }



    public void updateReservation(Reservation updatedReservation) {
        Reservation existing = reservationRepository.getReservationById(updatedReservation.getId());
        if (existing == null) {
            throw new RuntimeException("Rezerwacja nie znaleziona!");
        }
        if (updatedReservation.getCheckIn().isAfter(updatedReservation.getCheckOut())) {
            throw new RuntimeException("Data przyjazdu musi być wcześniejsza niż data wyjazdu!");
        }
        if (updatedReservation.getCheckIn().isBefore(LocalDate.now())) {
            throw new RuntimeException("Data przyjazdu nie może być w przeszłości!");
        }
        if (existing != null) {
            System.out.println("Aktualizacja roomId z " + existing.getRoomId() + " na " + updatedReservation.getRoomId());
            existing.setRoomId(updatedReservation.getRoomId());
            existing.setCheckIn(updatedReservation.getCheckIn());
            existing.setCheckOut(updatedReservation.getCheckOut());
        }
    }



    private boolean isRoomAvailable(Reservation newReservation) {
        return reservationRepository.getAllReservations().stream()
                // Zakładam, że używasz pola roomId – jeśli jest to inna właściwość identyfikująca dany pokój, zmień to odpowiednio
                .filter(existing -> existing.getRoomId().equals(newReservation.getRoomId()))
                // Warunek sprawdzający kolizję przedziałów czasowych:
                // Nowa rezerwacja koliduje z istniejącą, jeżeli jej data checkIn jest przed końcem istniejącej rezerwacji
                // oraz jej data checkOut jest po początku istniejącej rezerwacji
                .noneMatch(existing -> newReservation.getCheckIn().isBefore(existing.getCheckOut())
                        && newReservation.getCheckOut().isAfter(existing.getCheckIn()));
    }

    public List<Reservation> getUserReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reservationRepository.getAllReservations().stream()
                .filter(r -> username.equals(r.getUsername()))
                .collect(Collectors.toList());
    }

    public List<Reservation> getAllReservationsForAdmin() {
        return reservationRepository.getAllReservations();
    }

    public List<Reservation> getReservationsByHotelId(Long hotelId) {
        return reservationRepository.getAllReservations().stream()
                .filter(reservation -> reservation.getHotelId().equals(hotelId))
                .collect(Collectors.toList());
    }

    public List<Reservation> getUserReservationsByDate(LocalDate date) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reservationRepository.findByUsername(username).stream()
                .filter(r -> r.getCheckIn().equals(date) || r.getCheckOut().equals(date))
                .collect(Collectors.toList());
    }
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        if (reservation != null) {
            reservationRepository.removeReservation(reservationId);

        }


    }

    public List<Reservation> getReservationsByUsername(String username) {
        return reservationRepository.findByUsername(username);
    }

}
