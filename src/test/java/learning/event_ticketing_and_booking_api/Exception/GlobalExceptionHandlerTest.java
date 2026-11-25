package learning.event_ticketing_and_booking_api.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void accessDeniedReturnsForbidden() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        HttpServletRequest request = new MockHttpServletRequest();

        var response = exceptionHandler.handleAccessDeniedException(
                new AccessDeniedException("Not the reservation owner"), request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
