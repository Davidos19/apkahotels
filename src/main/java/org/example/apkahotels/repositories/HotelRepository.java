package org.example.apkahotels.repositories;

import org.example.apkahotels.models.Hotel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HotelRepository {
    private List<Hotel> hotels = new ArrayList<>();
    private Long currentId = 1L;

    public HotelRepository() {
        hotels = new ArrayList<>();
        hotels.add(new Hotel(currentId++, "Hotel Kraków", "Kraków", 10));
        hotels.add(new Hotel(currentId++, "Hotel Warszawa", "Warszawa", 5));
        hotels.add(new Hotel(currentId++, "Hotel Wieliczka", "Wieliczka", 14));
        hotels.add(new Hotel(currentId++, "Hotel Gdansk", "Gdansk", 7));
        hotels.add(new Hotel(currentId++, "Hotel Wroclaw", "Wroclaw", 18));
        hotels.add(new Hotel(currentId++, "Hotel Bochnia", "Bochnia", 3));
    }

    public List<Hotel> getAllHotels() {
        return hotels;
    }

    public Optional<Hotel> getHotelByIdOptional(Long id) {
        return hotels.stream().filter(h -> h.getId().equals(id)).findFirst();
    }

    public void addHotel(Hotel hotel) {
        hotel.setId(currentId++);
        hotels.add(hotel);
    }

    public void updateHotel(Hotel updatedHotel) {
        getHotelByIdOptional(updatedHotel.getId()).ifPresent(existingHotel -> {
            existingHotel.setName(updatedHotel.getName());
            existingHotel.setLocation(updatedHotel.getLocation());
            existingHotel.setAvailableRooms(updatedHotel.getAvailableRooms());
        });
    }

    public void deleteHotel(Long id) {
        hotels.removeIf(h -> h.getId().equals(id));
    }
    public Hotel getHotelById(Long id) {
        return hotels.stream()
                .filter(hotel -> hotel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}