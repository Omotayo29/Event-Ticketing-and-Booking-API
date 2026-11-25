package learning.event_ticketing_and_booking_api.DTO.Response;

import learning.event_ticketing_and_booking_api.model.ReservationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class ReservationResponse {
    private Long reservationId;
    private ReservationStatus  reservationStatus;
    private Integer quantity;
    private BigDecimal totalAmount;
    private OffsetDateTime expiresAt;

}
