package edu.guvi.dreamhome.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.guvi.dreamhome.Model.User;
import edu.guvi.dreamhome.Repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Userservice {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String name, String email, String password, String role) {
    if (userRepository.findByEmail(email).isPresent()) {
        throw new IllegalArgumentException("Email already exists");
    }

    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));

    if ("ADMIN".equalsIgnoreCase(role)) {
            user.setRole("ADMIN");
        } else {
            user.setRole("CUSTOMER");
        }

        return userRepository.save(user);
  }
}
