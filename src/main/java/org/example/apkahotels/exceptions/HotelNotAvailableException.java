package org.example.apkahotels.exceptions;

public class HotelNotAvailableException extends RuntimeException {
    public HotelNotAvailableException(String message) {
        super(message);
    }
}