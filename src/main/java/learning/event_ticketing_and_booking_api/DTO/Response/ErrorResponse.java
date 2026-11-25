package learning.event_ticketing_and_booking_api.DTO.Response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        String path,
        List<FieldValidationError> validationErrors
) {
}
