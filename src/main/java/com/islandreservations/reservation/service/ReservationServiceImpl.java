package com.islandreservations.reservation.service;

import com.islandreservations.reservation.ErrorMessage;
import com.islandreservations.reservation.converter.ReservationConverter;
import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.exception.IdempotencyKeyConflictException;
import com.islandreservations.reservation.exception.InvalidReservationStatusException;
import com.islandreservations.reservation.exception.ReservationAlreadyCancelledException;
import com.islandreservations.reservation.exception.ReservationException;
import com.islandreservations.reservation.exception.ReservationNotFoundException;
import com.islandreservations.reservation.model.Reservation;
import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.repository.ReservationRepository;
import com.islandreservations.reservation.service.producer.ReservationProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.islandreservations.reservation.ErrorMessage.RESERVATION_IS_NOT_IN_CHECKED_IN;
import static com.islandreservations.reservation.model.ReservationStatus.CHECKIN_REQUESTED;
import static com.islandreservations.reservation.model.ReservationStatus.REQUESTED;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationConverter reservationConverter;
    private final ReservationProducer reservationProducer;

    @Override
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        try {
            validateIdempotencyKey(reservationDTO);
            reservationDTO.setReservationStatus(REQUESTED);
            Reservation reservation = reservationConverter.toModel(reservationDTO);
            Reservation newReservation = reservationRepository.save(reservation);
            ReservationDTO newReservationDTO = reservationConverter.toDTO(newReservation);
            reservationProducer.createReservation(newReservationDTO);

            return newReservationDTO;
        } catch (OptimisticLockException e) {
            throw new ReservationException(ErrorMessage.CONCURRENT_UPDATE_CONFLICT, e.getMessage());
        }
    }

    @Override
    @Transactional
    public ReservationDTO cancelReservation(UUID reservationId) {

        return reservationRepository.findById(reservationId)
                .map(this::cancelReservation)
                .orElseThrow(() -> new ReservationNotFoundException(ErrorMessage.RESERVATION_NOT_FOUND.getMessage()));
    }

    private ReservationDTO cancelReservation(Reservation reservation) {
        if (!ReservationStatus.CANCELLED.equals(reservation.getReservationStatus())) {
            reservation.setReservationStatus(ReservationStatus.CANCEL_REQUESTED);
            Reservation cancelledReservation = reservationRepository.save(reservation);
            ReservationDTO cancelledReservationDTO = reservationConverter.toDTO(cancelledReservation);
            reservationProducer.cancelReservation(cancelledReservationDTO);

            return cancelledReservationDTO;
        } else {
            throw new ReservationAlreadyCancelledException(ErrorMessage.RESERVATION_ALREADY_CANCELED.getMessage());
        }
    }

    @Override
    @Transactional
    public ReservationDTO updateReservation(ReservationDTO updatedReservationDTO) {

        return reservationRepository.findById(updatedReservationDTO.getId())
                .map(existingReservation -> saveReservation(updatedReservationDTO, existingReservation))
                .orElseThrow(() -> new ReservationNotFoundException(ErrorMessage.RESERVATION_NOT_FOUND.formatMessage(updatedReservationDTO.getId())));
    }

    private ReservationDTO saveReservation(ReservationDTO updatedReservationDTO, Reservation existingReservation) {
        existingReservation.setFullName(updatedReservationDTO.getFullName());
        existingReservation.setEmail(updatedReservationDTO.getEmail());
        existingReservation.setStartDate(updatedReservationDTO.getStartDate());
        existingReservation.setEndDate(updatedReservationDTO.getEndDate());
        existingReservation.setReservationStatus(updatedReservationDTO.getReservationStatus());
        Reservation updatedReservation = reservationRepository.save(existingReservation);

        return reservationConverter.toDTO(updatedReservation);
    }

    private void validateIdempotencyKey(ReservationDTO reservationDTO) {
        if (!reservationRepository.findByIdempotencyKey(reservationDTO.getIdempotencyKey()).isEmpty()) {
            throw new IdempotencyKeyConflictException(ErrorMessage.RESERVATION_ALREADY_EXIST_WITH_SAME_IDEMPOTENCY.getMessage());
        }
    }

    private void validateIdempotencyKey(Reservation reservation) {
        validateIdempotencyKey(reservationConverter.toDTO(reservation));
    }

    @Override
    @Transactional
    public ReservationDTO checkIn(ReservationDTO reservationDTO) {

        return reservationRepository.findById(reservationDTO.getId())
                .map(reservation -> checkIn(reservationDTO, reservation))
                .orElseThrow(() -> new ReservationNotFoundException(ErrorMessage.RESERVATION_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional
    public ReservationDTO checkOut(UUID reservationId) {

        return reservationRepository.findById(reservationId)
                .map(this::checkOut)
                .orElseThrow(() -> new ReservationNotFoundException(ErrorMessage.RESERVATION_NOT_FOUND.getMessage()));
    }

    private ReservationDTO checkOut(Reservation reservation) {
        if (ReservationStatus.CHECKIN.equals(reservation.getReservationStatus())) {
            validateIdempotencyKey(reservation);
            reservation.setReservationStatus(ReservationStatus.CHECKOUT_REQUESTED);
            Reservation updatedReservation = reservationRepository.save(reservation);
            ReservationDTO reservationDTO = reservationConverter.toDTO(updatedReservation);
            reservationProducer.checkOut(reservationDTO);

            return reservationDTO;
        } else {
            throw new InvalidReservationStatusException(RESERVATION_IS_NOT_IN_CHECKED_IN.getMessage());
        }
    }

    private ReservationDTO checkIn(ReservationDTO reservationDTO, Reservation reservation) {
        if (ReservationStatus.CONFIRMED.equals(reservation.getReservationStatus())) {
            validateIdempotencyKey(reservation);
            reservation.setReservationStatus(CHECKIN_REQUESTED);
            reservationRepository.save(reservation);
            reservationProducer.checkIn(reservationDTO);
        } else {
            throw new InvalidReservationStatusException(ErrorMessage.INVALID_RESERVATION_STATUS_FOR_CHECKIN.getMessage());
        }

        return reservationConverter.toDTO(reservation);
    }

    @Override
    public ReservationDTO getReservationById(UUID reservationId) {

        return reservationRepository.findById(reservationId)
                .map(reservationConverter::toDTO)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + reservationId));
    }

    @Override
    public Page<ReservationDTO> getAllReservations(Pageable pageable) {
        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
        List<ReservationDTO> reservationDTOs = reservationPage.getContent().stream()
                .map(reservationConverter::toDTO)
                .toList();

        return new PageImpl<>(reservationDTOs, pageable, reservationPage.getTotalElements());
    }

    @Override
    public List<LocalDate> getAvailableDates(LocalDate startDate, LocalDate endDate) {
        List<Reservation> existingReservations = reservationRepository.findReservationsBetween(startDate, endDate);

        Set<LocalDate> reservedDates = existingReservations.stream()
                .flatMap(r -> getDatesBetween(r.getStartDate(), r.getEndDate()).stream())
                .collect(Collectors.toSet());

        return IntStream.range(0, (int) ChronoUnit.DAYS.between(startDate, endDate))
                .mapToObj(startDate::plusDays)
                .filter(date -> !reservedDates.contains(date))
                .toList();
    }

    private List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {

        return IntStream.range(0, (int) ChronoUnit.DAYS.between(startDate, endDate))
                .mapToObj(startDate::plusDays)
                .toList();
    }
}
