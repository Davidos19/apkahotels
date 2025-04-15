// InvalidReservationDateException.java
package org.example.apkahotels.exceptions;

public class InvalidReservationDateException extends RuntimeException {
    public InvalidReservationDateException(String message) {
        super(message);
    }
}