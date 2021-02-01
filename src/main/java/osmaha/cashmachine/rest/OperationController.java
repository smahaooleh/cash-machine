package osmaha.cashmachine.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import osmaha.cashmachine.dto.response.BalanceResponseDTO;
import osmaha.cashmachine.dto.response.MessageResponseDTO;
import osmaha.cashmachine.exception.OperationException;
import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.service.OperationService;

@RestController
@RequestMapping(value = "/api/v1/users")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @GetMapping(value = "/{id}/balance")
    public ResponseEntity getBalance(@PathVariable Long id) throws ResourceNotFoundException, OperationException {
        return new ResponseEntity<>(new BalanceResponseDTO(operationService.getBalance(id)), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/transactions")
    public ResponseEntity getTransaction(@PathVariable Long id) throws ResourceNotFoundException, OperationException {
        return new ResponseEntity<>(operationService.getTransactions(id), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/transactions/filter")
    public ResponseEntity getFilteredTransactionByType(@PathVariable Long id, @RequestParam String type) throws ResourceNotFoundException, IllegalArgumentException, OperationException {
        return new ResponseEntity<>(operationService.getTransactionsByType(id, type), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/withdraw")
    public ResponseEntity withdraw(@PathVariable Long id, @RequestParam Double amount) throws ResourceNotFoundException, OperationException {
        return new ResponseEntity<>(operationService.withdraw(id, amount), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/deposit", params = {"amount"})
    public ResponseEntity deposit(@PathVariable Long id, @RequestParam Double amount) throws ResourceNotFoundException, OperationException {
        return new ResponseEntity<>(operationService.deposit(id, amount), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/deposit", params = {"amount", "receiverCardNumber"})
    public ResponseEntity deposit(@PathVariable Long id, @RequestParam Double amount, @RequestParam String receiverCardNumber) throws ResourceNotFoundException, OperationException {
        operationService.deposit(id, amount, receiverCardNumber);
        return new ResponseEntity<>(new MessageResponseDTO("Deposit transaction for user with card number " + receiverCardNumber + " was processed successfully"), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/transfer")
    public ResponseEntity transfer(@PathVariable Long id, @RequestParam Double amount, @RequestParam String receiverCardNumber) throws ResourceNotFoundException, OperationException, IllegalArgumentException {
        return new ResponseEntity<>(operationService.transfer(id, amount, receiverCardNumber), HttpStatus.OK);
    }
}
