package learning.event_ticketing_and_booking_api.DTO.Response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketTierResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer capacity;
    private Integer availableCount;
}
