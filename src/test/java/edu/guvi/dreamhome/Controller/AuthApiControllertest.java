package edu.guvi.dreamhome.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.guvi.dreamhome.Security.CustomerUserDetailsService;
import edu.guvi.dreamhome.Security.Jwtutil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthApiControllertest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager; // ✅ mock AuthenticationManager

    @MockBean
    private Jwtutil jwtUtil; // ✅ mock Jwtutil (fixes your NoClassDefFoundError)

    @MockBean
    private CustomerUserDetailsService userDetailsService; // ✅ mock user details service

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLoginApi_ShouldReturnJwtToken() throws Exception {
        // Arrange
        Map<String, String> loginRequest = Map.of(
                "email", "test@gmail.com",
                "password", "12345"
        );

        UserDetails mockUser = User.withUsername("test@gmail.com")
                .password("encodedpass")
                .authorities("CUSTOMER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities()));

        when(userDetailsService.loadUserByUsername(eq("test@gmail.com"))).thenReturn(mockUser);
        when(jwtUtil.generateToken(eq("test@gmail.com"))).thenReturn("mock-jwt-token-12345");

        // Act & Assert
        mockMvc.perform(
                        post("/auth/api/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token-12345"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
}
