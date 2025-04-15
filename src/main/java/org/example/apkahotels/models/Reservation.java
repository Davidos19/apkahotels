package org.example.apkahotels.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    // Dodajemy pole do powiązania z pokojem
    private Long roomId;
    @ManyToOne(optional = false)
    private Room room;

    private LocalDate startDate;
    private LocalDate endDate;
    private String username;
    private LocalDate checkIn;
    private LocalDate checkOut;
}