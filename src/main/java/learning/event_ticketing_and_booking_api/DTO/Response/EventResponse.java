package learning.event_ticketing_and_booking_api.DTO.Response;

import learning.event_ticketing_and_booking_api.model.EventStatus;
import learning.event_ticketing_and_booking_api.model.TicketTiers;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class EventResponse {
    private long id;
    private String title;
    private String description;
    private EventStatus status;
    private String venue;
    private String city;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private List<TicketTierResponse> ticketTiers;
}
