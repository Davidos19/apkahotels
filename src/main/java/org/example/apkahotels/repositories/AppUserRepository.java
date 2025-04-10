package org.example.apkahotels.repositories;

import org.example.apkahotels.models.AppUser;

public interface AppUserRepository {
    AppUser findByUsername(String username);
    AppUser save(AppUser user);
}
