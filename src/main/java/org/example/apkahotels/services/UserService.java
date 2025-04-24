package org.example.apkahotels.services;

import org.example.apkahotels.models.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class UserService {
    // lepiej ConcurrentHashMap, ale HashMap też zadziała
    private final Map<String, AppUser> userStore = new HashMap<>();

    public void registerUser(AppUser newUser) {
        userStore.put(newUser.getUsername(), newUser);
    }

    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            // zamiast tworzyć nowego z dummy‑danymi, pobieramy z mapy
            return userStore.get(ud.getUsername());
        }
        return null;
    }

    public void updateUser(AppUser updatedUser) {
        userStore.put(updatedUser.getUsername(), updatedUser);
    }
}

