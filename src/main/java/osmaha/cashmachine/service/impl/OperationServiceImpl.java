package osmaha.cashmachine.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import osmaha.cashmachine.exception.OperationException;
import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.model.Transaction;
import osmaha.cashmachine.model.TransactionType;
import osmaha.cashmachine.model.User;
import osmaha.cashmachine.service.OperationService;
import osmaha.cashmachine.service.TransactionService;
import osmaha.cashmachine.service.UserService;

import java.util.List;

@Service
@Slf4j
public class OperationServiceImpl implements OperationService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Transactional(readOnly = true)
    @Override
    public double getBalance(Long id) throws ResourceNotFoundException, OperationException {
        User user = userService.getById(id);
        validateAccessForRequestedUser(user);
        log.info("IN getBalance - Balance was found for user with id {}", id);
        return user.getBalance();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getTransactions(Long id) throws ResourceNotFoundException, OperationException {
        User user = userService.getById(id);
        validateAccessForRequestedUser(user);
        List<Transaction> transactions = transactionService.getAllByUser(user);
        log.info("IN getTransactions - {} transactions were found for user with id {}", transactions.size(), id);
        return transactions;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getTransactionsByType(Long id, String type) throws ResourceNotFoundException, IllegalArgumentException, OperationException {
        TransactionType searchedType = TransactionType.valueOf(type);
        User user = userService.getById(id);
        validateAccessForRequestedUser(user);
        List<Transaction> transactions = transactionService.getAllByUserIdAndType(user, searchedType);
        log.info("IN getTransactionsByType - {} transactions with type {} were found for user with id {}", transactions.size(), searchedType.name(), id);
        return transactions;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {ResourceNotFoundException.class, OperationException.class})
    @Override
    public Transaction withdraw(Long id, Double amount) throws ResourceNotFoundException, OperationException {
        if (amount <= 0) {
            log.warn("IN withdraw - withdrawal with amount {} for user with id {} was failed: Operation amount can not be negative or zero", amount, id);
            throw new OperationException("Operation amount can not be negative or zero");
        }

        User user = userService.getById(id);
        validateAccessForRequestedUser(user);

        if (user.getBalance() - amount < 0) {
            log.warn("IN withdraw - withdrawal with amount {} for user with id {} was failed: Not enough money on the balance", amount, id);
            throw new OperationException("Not enough money on the balance");
        }

        double balance = user.getBalance();
        user.setBalance(balance - amount);

        log.info("IN withdraw - withdrawal with amount {} for user with id {} was successfully processed", amount, id);
        return transactionService.createWithdrawTransaction(user, amount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {ResourceNotFoundException.class, OperationException.class})
    @Override
    public Transaction deposit(Long id, Double amount) throws ResourceNotFoundException, OperationException {
        if (amount <= 0) {
            log.warn("IN deposit - deposit with amount {} for user with id {} was failed: Operation amount can not be negative or zero", amount, id);
            throw new OperationException("Operation amount can not be negative or zero");
        }

        User user = userService.getById(id);
        validateAccessForRequestedUser(user);

        double balance = user.getBalance();
        user.setBalance(balance + amount);

        log.info("IN deposit - deposit with amount {} for user with id {} was successfully processed", amount, id);
        return transactionService.createDepositTransaction(user, amount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {ResourceNotFoundException.class, OperationException.class})
    @Override
    public void deposit(Long id, Double amount, String receiverCardNumber) throws ResourceNotFoundException, OperationException {
        if (amount <= 0) {
            log.warn("IN deposit - deposit with amount {} from user with id {} for user with card number {} was failed: Operation amount can not be negative or zero", amount, id, receiverCardNumber);
            throw new OperationException("Operation amount can not be negative or zero");
        }
        if (receiverCardNumber.length() != 16) {
            log.warn("IN deposit - deposit with amount {} from user with id {} for user with card number {} was failed: Card number should have 16 digits", amount, id, receiverCardNumber);
            throw new IllegalArgumentException("Card number should have 16 digits");
        }

        User user = userService.getById(id);
        validateAccessForRequestedUser(user);

        validateDuplicateCardNumbers(user.getCardNumber(), receiverCardNumber);

        User receiver = userService.getByCardNumber(receiverCardNumber);

        double balance = receiver.getBalance();
        receiver.setBalance(balance + amount);

        log.info("IN deposit - deposit with amount {} from user with id {} for user with card number {} was successfully processed", amount, id, receiverCardNumber);
        transactionService.createDepositTransaction(receiver, amount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {ResourceNotFoundException.class, OperationException.class})
    @Override
    public Transaction transfer(Long id, Double amount, String receiverCardNumber) throws ResourceNotFoundException, OperationException, IllegalArgumentException {
        if (amount <= 0) {
            log.warn("IN transfer - transfer with amount {} from sender with id {} to receiver with cardNumber {} was failed: Operation amount can not be negative or zero", amount, id, receiverCardNumber);
            throw new OperationException("Operation amount can not be negative or zero");
        }
        if (receiverCardNumber.length() != 16) {
            log.warn("IN transfer - transfer with amount {} from sender with id {} to receiver with cardNumber {} was failed: Card number should have 16 digits", amount, id, receiverCardNumber);
            throw new IllegalArgumentException("Card number should have 16 digits");
        }

        User sender = userService.getById(id);

        validateAccessForRequestedUser(sender);
        validateDuplicateCardNumbers(sender.getCardNumber(), receiverCardNumber);

        if (sender.getBalance() - amount < 0) {
            log.warn("IN transfer - transfer with amount {} from sender with id {} to receiver with cardNumber {} was failed: Not enough money on the balance", amount, id, receiverCardNumber);
            throw new OperationException("Not enough money on the balance");
        }

        User receiver = userService.getByCardNumber(receiverCardNumber);

        double senderBalance = sender.getBalance();
        sender.setBalance(senderBalance - amount);

        double receiverBalance = receiver.getBalance();
        receiver.setBalance(receiverBalance + amount);

        log.info("IN transfer - transfer with amount {} from sender with id {} to receiver with cardNumber {} was successfully processed", amount, id, receiverCardNumber);
        return transactionService.createTransferTransactions(sender, amount, receiver);
    }

    private void validateAccessForRequestedUser(User user) throws OperationException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userDetails.getUsername().equals(user.getCardNumber())) {
            log.warn("IN validateAccessForRequestedUser - Access denied user with id {}", user.getId());
            throw new OperationException("Do not have access for this user");
        }
    }

    private void validateDuplicateCardNumbers(String senderCardNumber, String receiverCardNumber) throws OperationException {
        if (senderCardNumber.equals(receiverCardNumber)) {
            log.warn("IN validateDuplicateCardNumbers - sender and receiver card numbers are the same: {}", senderCardNumber);
            throw new OperationException("Sender and receiver card numbers shout not be the same");
        }
    }
}
