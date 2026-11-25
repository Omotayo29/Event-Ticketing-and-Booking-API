package learning.event_ticketing_and_booking_api.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "This cannot be empty")
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    @NotNull(message = "This cannot be empty")
    private String venue;
    @NotNull(message = "This cannot be empty")
    private String city;
    @NotNull(message = "This cannot be empty")
    private OffsetDateTime  startTime;
    @NotNull
    private OffsetDateTime endTime;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty
    private List<TicketTiers> ticketTiers;

    @PrePersist
    public void initialize() {
        if(status ==null)
            status = EventStatus.ACTIVE;
        if (ticketTiers == null)
            ticketTiers = new ArrayList<>();
    }

}
