package ua.ithillel.jwtAuth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.ithillel.jwtAuth.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
