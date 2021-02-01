package osmaha.cashmachine.service;

import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.model.Transaction;
import osmaha.cashmachine.model.TransactionType;
import osmaha.cashmachine.model.User;

import java.util.List;

public interface TransactionService {

    List<Transaction> getAllByUser(User user);

    List<Transaction> getAllByUserIdAndType(User user, TransactionType type);

    Transaction createWithdrawTransaction(User user, double amount);

    Transaction createDepositTransaction(User user, double amount);

    Transaction createTransferTransactions(User sender, double amount, User receiver);
}
