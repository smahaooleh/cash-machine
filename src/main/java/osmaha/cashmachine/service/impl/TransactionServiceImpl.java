package osmaha.cashmachine.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import osmaha.cashmachine.model.Transaction;
import osmaha.cashmachine.model.TransactionType;
import osmaha.cashmachine.model.User;
import osmaha.cashmachine.repository.TransactionRepository;
import osmaha.cashmachine.service.TransactionService;
import osmaha.cashmachine.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<Transaction> getAllByUser(User user) {
        List<Transaction> transactions = transactionRepository.findAllByUser(user);
        log.info("IN getAllByUser - {} transactions were found for user with id {}", transactions.size(), user.getId());
        return transactions;
    }

    @Override
    public List<Transaction> getAllByUserIdAndType(User user, TransactionType type) {
        List<Transaction> transactions = transactionRepository.findAllByUserAndType(user, type);
        log.info("IN getAllByUserIdAndType - {} transactions with type {} were found for user with id {}", transactions.size(), type.name(), user.getId());
        return transactions;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Transaction createWithdrawTransaction(User user, double amount) {
        Transaction transaction = new Transaction(user, -amount, user.getBalance(), LocalDateTime.now(), TransactionType.WITHDRAW);
        log.info("IN createWithdrawTransaction - transaction with amount {} and type {} for user with id {} was created", amount, TransactionType.WITHDRAW.name(), user.getId());
        return transactionRepository.save(transaction);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Transaction createDepositTransaction(User user, double amount) {
        Transaction transaction = new Transaction(user, amount, user.getBalance(), LocalDateTime.now(), TransactionType.DEPOSIT);
        log.info("IN createDepositTransaction - transaction with amount {} and type {} for user with id {} was created", amount, TransactionType.DEPOSIT.name(), user.getId());
        return transactionRepository.save(transaction);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Transaction createTransferTransactions(User sender, double amount, User receiver) {
        LocalDateTime now = LocalDateTime.now();
        Transaction senderTransaction = new Transaction(sender, -amount, sender.getBalance(), now, TransactionType.CREDITED);

        Transaction receiverTransaction = new Transaction(receiver, amount, receiver.getBalance(), now, TransactionType.DEBITED);

        Transaction savedSenderTransaction = transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);
        log.info("IN createTransferTransactions - transaction with amount {} and type {} for sender with id {} was created", -amount, TransactionType.CREDITED.name(), sender.getId());
        log.info("IN createTransferTransactions - transaction with amount {} and type {} for receiver with id {} was created", amount, TransactionType.DEBITED.name(), receiver.getId());
        return savedSenderTransaction;
    }
}
