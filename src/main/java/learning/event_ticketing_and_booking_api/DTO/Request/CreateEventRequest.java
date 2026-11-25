package learning.event_ticketing_and_booking_api.DTO.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CreateEventRequest {
    @NotNull(message = "This field cannot be null")
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;
    @NotNull(message = "This field cannot be null")
    @NotBlank
    @Size(min = 1)
    private String description;
    @NotNull(message = "This field cannot be null")
    @NotBlank
    private String venue;
    @NotNull(message = "This field cannot be null")
    @NotBlank
    private String city;
    @NotNull(message = "This field cannot be null")
    private OffsetDateTime startTime;
    @NotNull(message = "This field cannot be null")
    private OffsetDateTime endTime;
    @NotEmpty
    @Valid
    private List<TicketTierRequest> ticketTiers;
}
