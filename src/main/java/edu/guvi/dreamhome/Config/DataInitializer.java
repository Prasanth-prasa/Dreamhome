package edu.guvi.dreamhome.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.guvi.dreamhome.Model.User;
import edu.guvi.dreamhome.Repository.UserRepository;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@dreamhome.com").isEmpty()) {
                User admin = new User();
                admin.setName("prasanth");
                admin.setEmail("admin@dreamhome.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("✅ Default admin created: admin@dreamhome.com / admin123");
            }
        };
    }
}
