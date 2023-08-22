package com.islandreservations.reservation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReservationValidator.class)
@Documented
public @interface ValidReservation {
    String message() default "Invalid reservation";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}