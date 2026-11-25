package learning.event_ticketing_and_booking_api.mappers;

import learning.event_ticketing_and_booking_api.DTO.Request.CreateEventRequest;
import learning.event_ticketing_and_booking_api.DTO.Request.ReservationRequest;
import learning.event_ticketing_and_booking_api.DTO.Request.TicketTierRequest;
import learning.event_ticketing_and_booking_api.DTO.Request.UserRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.EventResponse;
import learning.event_ticketing_and_booking_api.DTO.Response.ReservationResponse;
import learning.event_ticketing_and_booking_api.DTO.Response.TicketTierResponse;
import learning.event_ticketing_and_booking_api.DTO.Response.UserResponse;
import learning.event_ticketing_and_booking_api.model.Event;
import learning.event_ticketing_and_booking_api.model.Reservation;
import learning.event_ticketing_and_booking_api.model.TicketTiers;
import learning.event_ticketing_and_booking_api.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class Mapping {
    public Event createEventRequestToEvent(CreateEventRequest createEventRequest){
        Event event = new Event();
        event.setTitle(createEventRequest.getTitle());
        event.setDescription(createEventRequest.getDescription());
        event.setStartTime(createEventRequest.getStartTime());
        event.setEndTime(createEventRequest.getEndTime());
        event.setCity(createEventRequest.getCity());
        event.setVenue(createEventRequest.getVenue());
        event.setTicketTiers(createTicketRequestToTicketTiers(createEventRequest.getTicketTiers(),event));
        return event;
    }

    public List<TicketTiers> createTicketRequestToTicketTiers(List<TicketTierRequest> ticket, Event event){
        List<TicketTiers> ticketTiers = new ArrayList<>();
        for(TicketTierRequest ticketTierRequest : ticket){
            ticketTiers.add(new TicketTiers(event,ticketTierRequest.getName(),ticketTierRequest.getPrice(),ticketTierRequest.getCapacity()));
        }
        return ticketTiers;
    }

    public List<TicketTierResponse> TicketTiersToTicketTiersResponse(List<TicketTiers> tiers){
        List<TicketTierResponse> ticketTierResponses = new ArrayList<>();
        for(TicketTiers ticketTiers : tiers){
            TicketTierResponse ticketTierResponse = new TicketTierResponse();
            ticketTierResponse.setName(ticketTiers.getName());
            ticketTierResponse.setPrice(ticketTiers.getPrice());
            ticketTierResponse.setId(ticketTiers.getId());
            ticketTierResponse.setCapacity(ticketTiers.getCapacity());
            ticketTierResponse.setAvailableCount(ticketTiers.getAvailableCount());
            ticketTierResponses.add(ticketTierResponse);
        }
        return ticketTierResponses;
    }

    public EventResponse EventToEventResponse(Event event){
        EventResponse eventResponse = new EventResponse();
        eventResponse.setId(event.getId());
        eventResponse.setTitle(event.getTitle());
        eventResponse.setDescription(event.getDescription());
        eventResponse.setStartTime(event.getStartTime());
        eventResponse.setEndTime(event.getEndTime());
        eventResponse.setTicketTiers(TicketTiersToTicketTiersResponse(event.getTicketTiers()));
        eventResponse.setStatus(event.getStatus());
        eventResponse.setVenue(event.getVenue());
        eventResponse.setCity(event.getCity());
        return eventResponse;
    }

    public ReservationResponse ReservationToReservationResponse(Reservation reservation, BigDecimal totalAmount){
        ReservationResponse reservationResponse = new ReservationResponse();
        reservationResponse.setReservationId(reservation.getId());
        reservationResponse.setQuantity(reservation.getQuantity());
        reservationResponse.setExpiresAt(reservation.getExpiresAt());
        reservationResponse.setReservationStatus(reservation.getStatus());
        reservationResponse.setTotalAmount(totalAmount);
        return reservationResponse;
    }

    public ReservationResponse ReservationToReservationResponse(Reservation reservation){
        ReservationResponse reservationResponse = new ReservationResponse();
        reservationResponse.setReservationId(reservation.getId());
        reservationResponse.setQuantity(reservation.getQuantity());
        reservationResponse.setExpiresAt(reservation.getExpiresAt());
        reservationResponse.setReservationStatus(reservation.getStatus());
        return reservationResponse;
    }

    public UserResponse UserRequestToUserDTO(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setUserRole(user.getRole());
        return userResponse;
    }
}
