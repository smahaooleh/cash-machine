package osmaha.cashmachine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osmaha.cashmachine.model.Transaction;
import osmaha.cashmachine.model.TransactionType;
import osmaha.cashmachine.model.User;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUser(User user);

    List<Transaction> findAllByUserAndType(User user, TransactionType type);

    Optional<Transaction> findById(Long id);

}
