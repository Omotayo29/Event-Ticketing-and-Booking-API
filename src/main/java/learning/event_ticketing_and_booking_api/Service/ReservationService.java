package learning.event_ticketing_and_booking_api.Service;

import learning.event_ticketing_and_booking_api.DTO.Request.ReservationRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.ReservationResponse;
import learning.event_ticketing_and_booking_api.Exception.*;
import learning.event_ticketing_and_booking_api.Repository.EventRepository;
import learning.event_ticketing_and_booking_api.Repository.ReservationRepository;
import learning.event_ticketing_and_booking_api.Repository.TicketTierRepository;
import learning.event_ticketing_and_booking_api.Repository.UserRepository;
import learning.event_ticketing_and_booking_api.mappers.Mapping;
import learning.event_ticketing_and_booking_api.model.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final TicketTierRepository ticketTierRepository;
    private final UserRepository userRepository;
    private final Mapping mapping;

    public ReservationService(ReservationRepository reservationRepository, EventRepository eventRepository, TicketTierRepository ticketTierRepository, UserRepository userRepository, Mapping mapping) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.ticketTierRepository = ticketTierRepository;
        this.userRepository = userRepository;
        this.mapping = mapping;
    }

    @Transactional
    public ReservationResponse createReservation(Long userId,Long eventId, ReservationRequest reservationRequest) {
        TicketTiers ticketTiers =  ticketTierRepository.findById(reservationRequest.getTierId()).orElseThrow(()-> new TierDoesNotExistException("This tier does not exist"));
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new EventDoesNotExistException("This event does not exist"));
        boolean tierBelongsToEvent = event.getTicketTiers().stream()
                .anyMatch(tier -> tier.getId().equals(ticketTiers.getId()));
        if (!tierBelongsToEvent) {
            throw new TierDoesNotExistException("This tier does not belong to this event");
        }
        User user = userRepository.findById(userId).orElseThrow(()-> new UserDoesNotExistException("This user does not exist"));
        if(reservationRepository.countByUserIdAndStatus(user.getId(),ReservationStatus.PENDING)>=2){
            throw new PendingReservationException("There is more than two pending reservations");
        }
        if(ticketTiers.getAvailableCount() < reservationRequest.getQuantity()){
            throw new AvailableCountException("The amount of space is less that quantity");
        }
        int value = ticketTiers.getAvailableCount();
        value-=reservationRequest.getQuantity();
        ticketTiers.setAvailableCount(value);
        Reservation reservation = new Reservation();
        int quantity = reservationRequest.getQuantity();
        reservation.setQuantity(quantity);
        reservation.setTier(ticketTiers);
        reservation.setUser(user);
        BigDecimal totalAmount = ticketTiers.getPrice().multiply(BigDecimal.valueOf(quantity));
        ticketTierRepository.save(ticketTiers);
        reservationRepository.save(reservation);
        return mapping.ReservationToReservationResponse(reservation,totalAmount);
    }

    public ReservationResponse getReservationById(Long reservationId, Long userId) {
        Reservation reservation =  reservationRepository.findById(reservationId).orElseThrow(()->new ReservationDoesNotExistException("This reservation does not exist."));
        ensureReservationOwner(reservation, userId);
        return mapping.ReservationToReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse confirmReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationDoesNotExistException("This reservation does not exist."));
        ensureReservationOwner(reservation, userId);
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationAlreadyProcessedException(
                    "Reservation cannot be confirmed because it is already " + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        return mapping.ReservationToReservationResponse(reservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation =  reservationRepository.findById(reservationId).orElseThrow(()->new ReservationDoesNotExistException("This reservation does not exist."));
        ensureReservationOwner(reservation, userId);
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationAlreadyProcessedException("Reservation cannot be cancelled because it is already " + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.getTier().setAvailableCount(reservation.getTier().getAvailableCount() + reservation.getQuantity());
        reservationRepository.save(reservation);
    }

    private void ensureReservationOwner(Reservation reservation, Long userId) {
        if (!reservation.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this reservation.");
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireReservations(){
        List<Reservation> reservations = reservationRepository.findReservationByStatusAndExpiresAtLessThanEqual(ReservationStatus.PENDING, OffsetDateTime.now());
        for (Reservation reservation : reservations) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservation.getTier().setAvailableCount(reservation.getTier().getAvailableCount() + reservation.getQuantity());
        }
        reservationRepository.saveAll(reservations);
    }
}
