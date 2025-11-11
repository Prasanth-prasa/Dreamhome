package edu.guvi.dreamhome.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
     @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        // Detect if it’s an API call
        if (request.getRequestURI().startsWith("/auth/api") ||
            request.getRequestURI().startsWith("/api")) {

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            error.put("path", request.getRequestURI());

            new ObjectMapper().writeValue(response.getOutputStream(), error);

        } else {
            // For web (Thymeleaf), redirect to login page
            response.sendRedirect("/auth/login?error=true");
        }
    }
}
