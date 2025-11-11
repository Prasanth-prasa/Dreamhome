package edu.guvi.dreamhome.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import edu.guvi.dreamhome.Security.CustomerUserDetailsService;
import edu.guvi.dreamhome.Security.Jwtutil;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthApiController {
    
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final Jwtutil jwtUtil;
    @Autowired
    private final CustomerUserDetailsService userDetailsService;

 @PostMapping(value = "/api/login", consumes = "application/json", produces = "application/json")
    public Map<String, Object> loginApi(@RequestBody Map<String, String> loginRequest) {

        System.out.println("🔹 Incoming JSON: " + loginRequest); // debug log

        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Map<String, Object> response = new HashMap<>();

        try {
            // 🔹 Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            // 🔹 Load user and generate token
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtUtil.generateToken(userDetails.getUsername());

            response.put("timestamp", Instant.now().toString());
            response.put("status", 200);
            response.put("email", userDetails.getUsername());
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("path", "/auth/api/login");
            return response;

        } catch (BadCredentialsException ex) {
            // 🔹 Wrong password
            response.put("timestamp", Instant.now().toString());
            response.put("status", 401);
            response.put("error", "Invalid username or password");
            response.put("path", "/auth/api/login");
            return response;

        } catch (Exception ex) {
            // 🔹 Any other error (DB, internal, etc.)
            response.put("timestamp", Instant.now().toString());
            response.put("status", 500);
            response.put("error", "Internal server error");
            response.put("path", "/auth/api/login");
            return response;
        }
    }
}
