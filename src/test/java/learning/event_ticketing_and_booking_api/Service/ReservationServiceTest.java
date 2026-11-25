package learning.event_ticketing_and_booking_api.Service;

import learning.event_ticketing_and_booking_api.DTO.Request.ReservationRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.ReservationResponse;
import learning.event_ticketing_and_booking_api.Exception.AvailableCountException;
import learning.event_ticketing_and_booking_api.Exception.EventDoesNotExistException;
import learning.event_ticketing_and_booking_api.Exception.PendingReservationException;
import learning.event_ticketing_and_booking_api.Exception.ReservationAlreadyProcessedException;
import learning.event_ticketing_and_booking_api.Exception.TierDoesNotExistException;
import learning.event_ticketing_and_booking_api.Exception.UserDoesNotExistException;
import learning.event_ticketing_and_booking_api.Repository.EventRepository;
import learning.event_ticketing_and_booking_api.Repository.ReservationRepository;
import learning.event_ticketing_and_booking_api.Repository.TicketTierRepository;
import learning.event_ticketing_and_booking_api.Repository.UserRepository;
import learning.event_ticketing_and_booking_api.mappers.Mapping;
import learning.event_ticketing_and_booking_api.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TicketTierRepository tierRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapping mapping;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReservationService reservationService;
    private User fakeUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        return user;
    }

    private Event fakeEvent() {
        Event event = new Event();
        event.setId(10L);
        event.setTitle("Java Conference");
        event.setTicketTiers(new ArrayList<>());
        return event;
    }

    private TicketTiers fakeTier(Event event) {
        TicketTiers tier = new TicketTiers(
                event,
                "VIP",
                new BigDecimal("50.00"),
                100
        );
        tier.setId(20L);
        tier.setAvailableCount(80);
        event.getTicketTiers().add(tier);
        return tier;
    }

    private Reservation fakeReservation(User user, TicketTiers tier) {
        Reservation reservation = new Reservation();
        reservation.setId(30L);
        reservation.setUser(user);
        reservation.setTier(tier);
        reservation.setQuantity(2);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(OffsetDateTime.now().minusMinutes(5));
        reservation.setExpiresAt(OffsetDateTime.now().plusMinutes(5));
        return reservation;
    }

    @Test
    void createReservation() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers ticketTiers = fakeTier(event);

        ReservationRequest request = new ReservationRequest();
        request.setTierId(20L);
        request.setQuantity(2);
        when(tierRepository.findById(20L))
                .thenReturn(Optional.of(ticketTiers));
        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(reservationRepository.countByUserIdAndStatus(
                1L, ReservationStatus.PENDING))
                .thenReturn(0);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation saved = invocation.getArgument(0);
                    saved.setId(30L);
                    saved.setStatus(ReservationStatus.PENDING);
                    return saved;
                });

        when(mapping.ReservationToReservationResponse(
                any(Reservation.class), any(BigDecimal.class)))
                .thenReturn(new ReservationResponse());

        ReservationResponse reservationResponse = reservationService.createReservation(1L, 10L, request);

        assertEquals(78, ticketTiers.getAvailableCount());
        assertNotNull(reservationResponse);
        verify(mapping).ReservationToReservationResponse(
                argThat(saved -> saved.getId().equals(30L)
                        && saved.getUser().equals(user)
                        && saved.getTier().equals(ticketTiers)
                        && saved.getQuantity().equals(2)
                        && saved.getStatus() == ReservationStatus.PENDING),
                eq(new BigDecimal("100.00")));
        verify(tierRepository).save(ticketTiers);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_throwsWhenTierDoesNotExist() {
        ReservationRequest request = new ReservationRequest();
        request.setTierId(20L);
        request.setQuantity(2);
        when(tierRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(TierDoesNotExistException.class,
                () -> reservationService.createReservation(1L, 10L, request));
        verify(tierRepository).findById(20L);
        verifyNoInteractions(eventRepository, userRepository, reservationRepository);
    }

    @Test
    void createReservation_throwsWhenEventDoesNotExist() {
        Event event = fakeEvent();
        TicketTiers tier = fakeTier(event);
        ReservationRequest request = new ReservationRequest();
        request.setTierId(20L);
        request.setQuantity(2);
        when(tierRepository.findById(20L)).thenReturn(Optional.of(tier));
        when(eventRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EventDoesNotExistException.class,
                () -> reservationService.createReservation(1L, 10L, request));
        verify(eventRepository).findById(10L);
        verifyNoInteractions(userRepository, reservationRepository);
    }

    @Test
    void createReservation_throwsWhenTierBelongsToAnotherEvent() {
        Event requestedEvent = fakeEvent();
        Event tierEvent = fakeEvent();
        tierEvent.setId(11L);
        TicketTiers tier = fakeTier(tierEvent);
        ReservationRequest request = new ReservationRequest();
        request.setTierId(20L);
        request.setQuantity(2);
        when(tierRepository.findById(20L)).thenReturn(Optional.of(tier));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(requestedEvent));

        assertThrows(TierDoesNotExistException.class,
                () -> reservationService.createReservation(1L, 10L, request));
        verifyNoInteractions(userRepository, reservationRepository);
    }

    @Test
    void createReservation_throwsWhenUserDoesNotExist() {
        Event event = fakeEvent();
        TicketTiers tier = fakeTier(event);
        ReservationRequest request = new ReservationRequest();
        request.setTierId(20L);
        request.setQuantity(2);
        when(tierRepository.findById(20L)).thenReturn(Optional.of(tier));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class,
                () -> reservationService.createReservation(1L, 10L, request));
        verify(userRepository).findById(1L);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void createReservation_throwsWhenUserHasTwoPendingReservations() {
        Event event = fakeEvent();
        TicketTiers tier = fakeTier(event);
        ReservationRequest request = new ReservationRequest();
        request.setTierId(20L);
        request.setQuantity(2);
        when(tierRepository.findById(20L)).thenReturn(Optional.of(tier));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser()));
        when(reservationRepository.countByUserIdAndStatus(1L, ReservationStatus.PENDING))
                .thenReturn(2);

        assertThrows(PendingReservationException.class,
                () -> reservationService.createReservation(1L, 10L, request));
        verify(reservationRepository).countByUserIdAndStatus(1L, ReservationStatus.PENDING);
        verifyNoInteractions(mapping);
    }

    @Test
    void createReservation_throwsWhenAvailabilityIsInsufficient() {
        Event event = fakeEvent();
        TicketTiers tier = fakeTier(event);
        tier.setAvailableCount(1);
        ReservationRequest request = new ReservationRequest();
        request.setTierId(20L);
        request.setQuantity(2);
        when(tierRepository.findById(20L)).thenReturn(Optional.of(tier));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser()));
        when(reservationRepository.countByUserIdAndStatus(1L, ReservationStatus.PENDING))
                .thenReturn(0);

        assertThrows(AvailableCountException.class,
                () -> reservationService.createReservation(1L, 10L, request));
        assertEquals(1, tier.getAvailableCount());
        verifyNoInteractions(mapping);
    }

    @Test
    void getReservationById() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers tiers =fakeTier(event);
        Reservation reservation = fakeReservation(user,tiers);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.getReservationById(1L, 1L);
        verify(reservationRepository).findById(1L);
    }

    @Test
    void getReservationById_throwsWhenUserIsNotOwner() {
        Reservation reservation = fakeReservation(fakeUser(), fakeTier(fakeEvent()));
        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));

        assertThrows(AccessDeniedException.class,
                () -> reservationService.getReservationById(30L, 2L));
        verifyNoInteractions(mapping);
    }

    @Test
    void cancelReservation() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers tiers =fakeTier(event);
        Reservation reservation = fakeReservation(user,tiers);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        reservationService.cancelReservation(1L, 1L);

        assertEquals(82, tiers.getAvailableCount());
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void cancelReservation_throwsWhenUserIsNotOwner() {
        User user = fakeUser();
        TicketTiers tier = fakeTier(fakeEvent());
        Reservation reservation = fakeReservation(user, tier);
        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));

        assertThrows(AccessDeniedException.class,
                () -> reservationService.cancelReservation(30L, 2L));
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(80, tier.getAvailableCount());
        verify(reservationRepository, org.mockito.Mockito.never()).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_throwsWhenAlreadyProcessed() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers tier = fakeTier(event);
        Reservation reservation = fakeReservation(user, tier);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(ReservationAlreadyProcessedException.class,
                () -> reservationService.cancelReservation(1L, 1L));
        assertEquals(80, tier.getAvailableCount());
        verify(reservationRepository).findById(1L);
    }

    @Test
    void confirmReservation_changesPendingReservationToConfirmed() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers tier = fakeTier(event);
        Reservation reservation = fakeReservation(user, tier);

        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));
        when(mapping.ReservationToReservationResponse(reservation))
                .thenReturn(new ReservationResponse());

        ReservationResponse response = reservationService.confirmReservation(30L, 1L);

        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        assertNotNull(response);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void confirmReservation_throwsWhenAlreadyProcessed() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers tier = fakeTier(event);
        Reservation reservation = fakeReservation(user, tier);
        reservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));

        assertThrows(ReservationAlreadyProcessedException.class,
                () -> reservationService.confirmReservation(30L, 1L));
        verify(reservationRepository).findById(30L);
        verify(reservationRepository, org.mockito.Mockito.never()).save(any(Reservation.class));
        verifyNoInteractions(mapping);
    }

    @Test
    void confirmReservation_throwsWhenUserIsNotOwner() {
        Reservation reservation = fakeReservation(fakeUser(), fakeTier(fakeEvent()));
        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));

        assertThrows(AccessDeniedException.class,
                () -> reservationService.confirmReservation(30L, 2L));
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        verify(reservationRepository, org.mockito.Mockito.never()).save(any(Reservation.class));
        verifyNoInteractions(mapping);
    }

    @Test
    void expireReservations_marksExpiredReservationAndReleasesTickets() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers tiers =fakeTier(event);
        Reservation reservation = fakeReservation(user,tiers);
        reservation.setExpiresAt(OffsetDateTime.now().minusSeconds(5));
        when(reservationRepository.findReservationByStatusAndExpiresAtLessThanEqual(
                eq(ReservationStatus.PENDING), any(OffsetDateTime.class)))
                .thenReturn(List.of(reservation));

        reservationService.expireReservations();

        assertEquals(ReservationStatus.EXPIRED, reservation.getStatus());
        assertEquals(82, tiers.getAvailableCount());
        verify(reservationRepository).saveAll(List.of(reservation));
    }

    @Test
    void pendingReservationBecomeConfirmed() {
        User user = fakeUser();
        Event event = fakeEvent();
        TicketTiers tiers =fakeTier(event);
        Reservation reservation = fakeReservation(user,tiers);
        when(reservationRepository.findById(30L)).thenReturn(Optional.of(reservation));
        reservationService.confirmReservation(reservation.getId(), user.getId());
        verify(reservationRepository).save(reservation);
    }
}