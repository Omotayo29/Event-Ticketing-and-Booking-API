package learning.event_ticketing_and_booking_api.DTO.Request;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class ReservationRequest {
    @NotNull
    private Long tierId;
    @Positive
    @NotNull
    @Max(5)
    private Integer quantity;
}
