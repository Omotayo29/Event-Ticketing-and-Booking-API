package learning.event_ticketing_and_booking_api.Controller;

import jakarta.validation.Valid;
import learning.event_ticketing_and_booking_api.DTO.Request.CreateEventRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.EventResponse;
import learning.event_ticketing_and_booking_api.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1")
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("events")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest createEventRequest){
        return new ResponseEntity<>(eventService.createEvent(createEventRequest), HttpStatus.CREATED);
    }
    @GetMapping("events")
    public ResponseEntity<Page<EventResponse>> getAllEvents(Pageable pageable){
        return new ResponseEntity<>(eventService.getEvents(pageable),HttpStatus.OK);
    }
    @GetMapping("events/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id){
        return ResponseEntity.ok(eventService.getEventByID(id));
    }

    @DeleteMapping("events/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long id){
        eventService.cancelEvent(id);
        return ResponseEntity.noContent().build();
    }
}
