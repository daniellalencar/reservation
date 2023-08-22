package com.islandreservations.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.islandreservations.reservation.controller.ReservationController;
import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.model.Reservation;
import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.repository.ReservationRepository;
import com.islandreservations.reservation.service.ReservationService;
import com.islandreservations.reservation.service.producer.ReservationProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private ReservationProducer reservationProducer;



    @InjectMocks
    private ReservationController reservationController;


    @Test
    public void createReservationTest() throws Exception {
        ReservationDTO reservationDTO = createReservationDTO();
        mockFindByAndSave(reservationDTO.getId());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/volcano-island/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isOk());
    }

    private ReservationDTO createReservationDTO() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setFullName("John Doe");
        reservationDTO.setEmail("johndoe@example.com");
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(7); // 1 week from now
        LocalDate endDate = startDate.plusDays(3); // 4 days stay
        reservationDTO.setStartDate(startDate);
        reservationDTO.setEndDate(endDate);
        reservationDTO.setReservationStatus(ReservationStatus.PENDING);
        reservationDTO.setIdempotencyKey(UUID.randomUUID());
        reservationDTO.setId(UUID.randomUUID());
        return reservationDTO;
    }

    @Test
    public void updateReservationTest() throws Exception {
        ReservationDTO reservationDTO = createReservationDTO();
        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationDTO.getId());
        mockReservation.setReservationStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(mockReservation.getId())).thenReturn(Optional.of(mockReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/volcano-island/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllReservationsTest() throws Exception {
        Reservation mockReservation = new Reservation();


        // Mock the Pageable
        Pageable pageable = mock(Pageable.class);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getPageNumber()).thenReturn(1);
        when(pageable.getSort()).thenReturn(Sort.unsorted());

        // Create some mock reservations
        Reservation reservation1 = new Reservation();
        reservation1.setId(UUID.randomUUID());
        Reservation reservation2 = new Reservation();
        reservation2.setId(UUID.randomUUID());
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

        // Mock the Page
        Page<Reservation> page = new PageImpl<>(reservations);
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/volcano-island/reservations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void cancelReservationTest() throws Exception {
        UUID reservationId = UUID.randomUUID();
        mockFindByAndSave(reservationId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/volcano-island/" + reservationId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkInTest() throws Exception {
        ReservationDTO reservationDTO = createReservationDTO();
        mockFindByAndSave(reservationDTO.getId());
        doNothing().when(reservationProducer).checkIn(Mockito.any(ReservationDTO.class));
        
        mockMvc.perform(MockMvcRequestBuilders.put("/api/volcano-island/" + reservationDTO.getId() + "/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isOk());
    }

    private void mockFindByAndSave(UUID reservationDTO) {
        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationDTO);
        mockReservation.setReservationStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(reservationDTO)).thenReturn(Optional.of(mockReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation);
    }

    @Test
    public void checkOutTest() throws Exception {

        ReservationDTO reservationDTO = createReservationDTO();

        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationDTO.getId());
        mockReservation.setReservationStatus(ReservationStatus.CHECKIN);
        when(reservationRepository.findById(reservationDTO.getId())).thenReturn(Optional.of(mockReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation);
        doNothing().when(reservationProducer).checkOut(Mockito.any(ReservationDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/volcano-island/" + reservationDTO.getId() + "/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void getReservationTest() throws Exception {
        ReservationDTO reservationDTO = createReservationDTO();
        mockFindByAndSave(reservationDTO.getId());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/volcano-island/" + reservationDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

