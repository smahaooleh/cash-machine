package osmaha.cashmachine.service;

import osmaha.cashmachine.exception.OperationException;
import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.model.Transaction;

import java.util.List;

public interface OperationService {

    double getBalance(Long id) throws ResourceNotFoundException, OperationException;

    List<Transaction> getTransactions(Long id) throws ResourceNotFoundException, OperationException;

    List<Transaction> getTransactionsByType(Long id, String type) throws ResourceNotFoundException, IllegalArgumentException, OperationException;

    Transaction withdraw(Long id, Double amount) throws ResourceNotFoundException, OperationException;

    Transaction deposit(Long id, Double amount) throws ResourceNotFoundException, OperationException;

    void deposit(Long id, Double amount, String receiverCardNumber) throws ResourceNotFoundException, OperationException;

    Transaction transfer(Long id, Double amount, String receiverCardNumber) throws ResourceNotFoundException, OperationException, IllegalArgumentException;

}
