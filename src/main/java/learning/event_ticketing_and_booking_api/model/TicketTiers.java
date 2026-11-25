package learning.event_ticketing_and_booking_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@RequiredArgsConstructor
public class TicketTiers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @NonNull
    private Event event;
    @NotNull
    @NonNull
    private String name;
    @NotNull
    @NonNull
    private BigDecimal price;
    @NotNull
    @NonNull
    private Integer capacity;
    @NotNull
    private Integer availableCount;
    @Version
    private Integer version;

    public TicketTiers() {
    }
}
