package edu.guvi.dreamhome.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler{
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String requestUri = request.getRequestURI();

        // ✅ For API requests — send JSON instead of HTML
        if (requestUri.startsWith("/auth/api") || requestUri.startsWith("/api")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            error.put("path", requestUri);

            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } else {
            // ✅ For normal web form (Thymeleaf)
            response.sendRedirect("/login?error=true");
        }
    }
}
