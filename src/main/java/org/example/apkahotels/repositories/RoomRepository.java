package org.example.apkahotels.repositories;

import org.example.apkahotels.models.Room;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RoomRepository {
    private final List<Room> rooms = new ArrayList<>();
    private Long currentId = 1L;

    public RoomRepository() {
        // Przykładowe pokoje dla hotelu o ID 1
        addRoom(new Room(null, 1L, "Standard", 2, 150.0, "https://source.unsplash.com/400x300/?standard,room", "101"));
        addRoom(new Room(null, 1L, "Deluxe", 3, 250.0, "https://source.unsplash.com/400x300/?deluxe,room", "102"));

        // Przykładowe pokoje dla hotelu o ID 2
        addRoom(new Room(null, 2L, "Standard", 2, 120.0, "https://source.unsplash.com/400x300/?standard,room", "201"));
        addRoom(new Room(null, 2L, "Suite", 4, 300.0, "https://source.unsplash.com/400x300/?suite,room", "202"));

        addRoom(new Room(null, 3L, "Standard", 2, 150.0, "https://source.unsplash.com/400x300/?standard,room", "321"));
        addRoom(new Room(null, 3L, "Suite", 4, 300.0, "https://source.unsplash.com/400x300/?suite,room", "321"));

        addRoom(new Room(null, 4L, "Standard", 2, 170.0, "https://source.unsplash.com/400x300/?standard,room", "420"));
        addRoom(new Room(null, 4L, "Deluxe", 4, 500.0, "https://source.unsplash.com/400x300/?suite,room", "444"));

        addRoom(new Room(null, 5L, "Standard", 2, 220.0, "https://source.unsplash.com/400x300/?standard,room", "501"));
        addRoom(new Room(null, 5L, "Deluxe", 4, 500.0, "https://source.unsplash.com/400x300/?suite,room", "588"));
        addRoom(new Room(null, 2L, "Suite", 4, 300.0, "https://source.unsplash.com/400x300/?suite,room", "544"));
    }


    public List<Room> getAllRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        room.setId(currentId++);
        rooms.add(room);
    }

    public List<Room> findByHotelId(Long hotelId) {
        return rooms.stream()
                .filter(room -> room.getHotelId() != null && room.getHotelId().equals(hotelId))
                .collect(Collectors.toList());
    }
    public Room getRoomById(Long id) {
        return rooms.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Room> getRoomsByHotelId(Long hotelId) {
        return rooms.stream()
                .filter(r -> r.getHotelId()!= null && r.getHotelId().equals(hotelId))
                .collect(Collectors.toList());
    }

    public void removeRoom(Long id) {
        rooms.removeIf(r -> r.getId().equals(id));
    }

}

