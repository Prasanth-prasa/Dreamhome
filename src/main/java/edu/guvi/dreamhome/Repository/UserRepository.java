package edu.guvi.dreamhome.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.guvi.dreamhome.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);
   
}
