package org.example.apkahotels.repositories;

import org.example.apkahotels.models.AppUser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAppUserRepository implements AppUserRepository {

    private final List<AppUser> users = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public AppUser findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public AppUser save(AppUser user) {
        // jeśli update (już istnieje), usuń starą wersję
        users.removeIf(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));

        // jeśli nowy (bez ID), nadaj ID
        if (user.getId() == null) {
            user.setId(idGen.getAndIncrement());
        }

        users.add(user);
        return user;
    }
}

