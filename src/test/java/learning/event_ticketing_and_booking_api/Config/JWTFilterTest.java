package learning.event_ticketing_and_booking_api.Config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import learning.event_ticketing_and_booking_api.Service.JWTService;
import learning.event_ticketing_and_booking_api.Service.MyUsersDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JWTFilterTest {

    @Test
    void malformedTokenReturnsUnauthorized() throws Exception {
        JWTService jwtService = mock(JWTService.class);
        MyUsersDetailService usersDetailService = mock(MyUsersDetailService.class);
        when(jwtService.extractName("broken"))
                .thenThrow(new MalformedJwtException("Malformed token"));
        JWTFilter filter = new JWTFilter(jwtService, usersDetailService);
        MockHttpServletRequest request = requestWithToken("broken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new org.springframework.mock.web.MockFilterChain());

        assertEquals(401, response.getStatus());
        verifyNoInteractions(usersDetailService);
    }

    @Test
    void expiredTokenReturnsUnauthorized() throws Exception {
        JWTService jwtService = mock(JWTService.class);
        MyUsersDetailService usersDetailService = mock(MyUsersDetailService.class);
        when(jwtService.extractName("expired"))
                .thenThrow(new ExpiredJwtException(null, null, "Expired token"));
        JWTFilter filter = new JWTFilter(jwtService, usersDetailService);
        MockHttpServletRequest request = requestWithToken("expired");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new org.springframework.mock.web.MockFilterChain());

        assertEquals(401, response.getStatus());
        verifyNoInteractions(usersDetailService);
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
