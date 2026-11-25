package learning.event_ticketing_and_booking_api.DTO.Request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketTierRequest {
    @NotBlank
    private String name;
    @NotNull(message = "This field cannot be null")
    @DecimalMin("0.0")
    private BigDecimal price;
    @NotNull(message = "This field cannot be null")
    @Positive
    private Integer capacity;
}
