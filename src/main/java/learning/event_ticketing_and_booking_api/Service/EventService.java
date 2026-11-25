package learning.event_ticketing_and_booking_api.Service;

import learning.event_ticketing_and_booking_api.DTO.Request.CreateEventRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.EventResponse;
import learning.event_ticketing_and_booking_api.Exception.EventDoesNotExistException;
import learning.event_ticketing_and_booking_api.Exception.InvalidDateException;
import learning.event_ticketing_and_booking_api.Repository.EventRepository;
import learning.event_ticketing_and_booking_api.mappers.Mapping;
import learning.event_ticketing_and_booking_api.model.Event;
import learning.event_ticketing_and_booking_api.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final Mapping mapping;

    public EventService(EventRepository eventRepository, Mapping mapping) {
        this.eventRepository = eventRepository;
        this.mapping = mapping;
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest createEventRequest){
        if(createEventRequest.getStartTime().isBefore(OffsetDateTime.now()) || !createEventRequest.getEndTime().isAfter(createEventRequest.getStartTime())){
            throw new InvalidDateException("Invalid date");
        }
        Event event = mapping.createEventRequestToEvent(createEventRequest);
        event.getTicketTiers().forEach(tier -> tier.setAvailableCount(tier.getCapacity()));
        eventRepository.save(event);
        return mapping.EventToEventResponse(event);
    }
    public EventResponse getEventByID(Long eventID){
        return eventRepository.findById(eventID).map(mapping::EventToEventResponse).orElseThrow(() -> new EventDoesNotExistException("There is no such event"));
    }
    public Page<EventResponse> getEvents(Pageable pageable){
        return eventRepository.findAll(pageable).map(mapping::EventToEventResponse);
    }
    public void cancelEvent(Long eventID){
        Event event = eventRepository.findById(eventID).orElseThrow(() -> new EventDoesNotExistException("There is no such event"));
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }
}

