package osmaha.cashmachine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osmaha.cashmachine.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCardNumber(String cardNumber);
}
