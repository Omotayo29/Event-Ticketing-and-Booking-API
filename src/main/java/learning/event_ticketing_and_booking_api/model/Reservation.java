package learning.event_ticketing_and_booking_api.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "tier_id",  nullable = false)
    private TicketTiers tier;
    @NotNull
    @Positive
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @NotNull
    private OffsetDateTime createdAt;
    @NotNull
    private OffsetDateTime expiresAt;

    @PrePersist
    public void initialize(){
        if (this.createdAt == null)
            createdAt = OffsetDateTime.now();
        if(this.expiresAt == null)
            expiresAt = createdAt.plusMinutes(10);
        if(this.status  == null)
            status = ReservationStatus.PENDING;
    }
}
