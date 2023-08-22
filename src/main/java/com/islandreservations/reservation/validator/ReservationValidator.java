package com.islandreservations.reservation.validator;

import com.islandreservations.reservation.dto.ReservationDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class ReservationValidator implements ConstraintValidator<ValidReservation, ReservationDTO> {
    @Override
    public void initialize(ValidReservation constraintAnnotation) {
    }

    @Override
    public boolean isValid(ReservationDTO reservationRequest, ConstraintValidatorContext context) {
        LocalDate arrivalDate = reservationRequest.getStartDate();
        LocalDate departureDate = reservationRequest.getEndDate();

        if (arrivalDate == null || departureDate == null) {
            return false;
        }

        long daysBetween = ChronoUnit.DAYS.between(arrivalDate, departureDate);
        if (daysBetween < 1 || daysBetween > 3) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Reservation must be for 1 to 3 days").addConstraintViolation();
            return false;
        }

        LocalDate now = LocalDate.now();
        if (!arrivalDate.isAfter(now.plusDays(1)) || !arrivalDate.isBefore(now.plusMonths(1))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Reservation must be made minimum 1 day and maximum 1 month in advance").addConstraintViolation();
            return false;
        }

        return true;
    }
}
