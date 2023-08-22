package com.islandreservations.reservation.exception;


import com.islandreservations.reservation.ErrorMessage;

public class ReservationException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ReservationException(ErrorMessage errorMessage, Object... args) {
        super(errorMessage.formatMessage(args));
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
