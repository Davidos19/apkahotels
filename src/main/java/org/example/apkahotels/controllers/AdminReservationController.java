package org.example.apkahotels.controllers;

import org.example.apkahotels.models.Reservation;
import org.example.apkahotels.services.ReservationService;
import org.example.apkahotels.services.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;
    public AdminReservationController(ReservationService reservationService,  RoomService roomService) {
        this.reservationService = reservationService;
        this.roomService        = roomService;
    }

    // Lista rezerwacji dla admina (już istniejąca)
    @GetMapping
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservationsForAdmin());
        return "admin_reservations";
    }

    // Formularz edycji rezerwacji
    @GetMapping("/edit/{id}")
    public String editReservation(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return "redirect:/admin/reservations?error=notfound";
        }
        model.addAttribute("reservation", reservation);
        // dodajemy listę pokoi z hotelu tej rezerwacji
        model.addAttribute("rooms",
                roomService.getRoomsByHotelId(reservation.getHotelId()));
        return "admin_edit_reservation";
    }

    // Aktualizacja rezerwacji – zapis zmian
    @PostMapping("/update")
    public String updateReservation(@ModelAttribute Reservation reservation,
                                    RedirectAttributes attrs) {
        try {
            reservationService.updateReservation(reservation);
            attrs.addFlashAttribute("message", "Rezerwacja zaktualizowana");
        } catch (RuntimeException ex) {
            attrs.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/reservations/edit/" + reservation.getId();
        }
        // przekierowujemy z powrotem do listy rezerwacji dla tego hotelu
        return "redirect:/admin/hotels/"
                + reservation.getHotelId()
                + "/reservations";
    }
}