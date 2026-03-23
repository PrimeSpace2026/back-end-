package primespace.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import primespace.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    boolean existsByEmail(String email);
}
