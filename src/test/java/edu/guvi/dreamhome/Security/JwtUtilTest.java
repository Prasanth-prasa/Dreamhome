package edu.guvi.dreamhome.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


public class JwtUtilTest {

    @Autowired
    private Jwtutil jwtUtil;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new Jwtutil();
        // Simulate injected values
        jwtUtil.secretKey = "bXlTZWNyZXRLZXlGb3JKV1RUb2tlbkdlbmVyYXRpb25BbmRWYWxpZGF0aW9uUHVycG9zZXM=";
        jwtUtil.expirationTime = 3600;
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("testuser@example.com");

        assertNotNull(token);
        assertEquals("testuser@example.com", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, "testuser@example.com"));
    }

    @Test
    void testTokenShouldExpire() throws InterruptedException {
        jwtUtil.expirationTime = 1; // 1 second
        String token = jwtUtil.generateToken("prasanthprasa4.com");

        Thread.sleep(1500); // wait till expiry
        assertFalse(jwtUtil.validateToken(token, "prasanthprasa4.com"));
    }
}
