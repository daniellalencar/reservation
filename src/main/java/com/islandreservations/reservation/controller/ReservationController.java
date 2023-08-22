package com.islandreservations.reservation.controller;

import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@RestController
@ResponseBody
@RequiredArgsConstructor
@RequestMapping(value = {"/api/volcano-island/"})
public class ReservationController {

    private final ReservationService reservationService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reservation")
    public ReservationDTO createReservation(@RequestBody @Valid ReservationDTO reservationDTO) {
        return reservationService.createReservation(reservationDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/reservation")
    public ReservationDTO updateReservation(@RequestBody @Valid ReservationDTO reservationDTO) {
        return reservationService.updateReservation(reservationDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/available-dates")
    public Collection<LocalDate> getAvailableDates(
            LocalDate startDate,
            LocalDate endDate) {
        return reservationService.getAvailableDates(startDate, endDate);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/reservations")
    public Page<ReservationDTO> getAllReservations(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return reservationService.getAllReservations(pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{reservationId}/cancel")
    public ReservationDTO cancelReservation(@PathVariable UUID reservationId) {
        return reservationService.cancelReservation(reservationId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{reservationId}/checkin")
    public ReservationDTO checkIn(@PathVariable UUID reservationId, @RequestBody ReservationDTO reservationDTO) {
        reservationDTO.setId(reservationId);
        return reservationService.checkIn(reservationDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{reservationId}/checkout")
    public void checkOut(@PathVariable UUID reservationId, @RequestBody ReservationDTO reservationDTO) {
        reservationDTO.setId(reservationId);
        reservationService.checkOut(reservationId);
    }

    @GetMapping("/{reservationId}")
    public ReservationDTO getReservation(@PathVariable UUID reservationId) {
        return reservationService.getReservationById(reservationId);
    }
}
