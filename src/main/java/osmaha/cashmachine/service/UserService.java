package osmaha.cashmachine.service;

import osmaha.cashmachine.dto.request.RegistrationRequestDTO;
import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.model.User;
import osmaha.cashmachine.exception.RegistrationException;

import java.util.List;

public interface UserService {

    User register(RegistrationRequestDTO userDTO) throws IllegalArgumentException, RegistrationException;

    List<User> getAll();

    User getByCardNumber(String cardNumber) throws IllegalArgumentException, ResourceNotFoundException;

    User getById(Long id) throws ResourceNotFoundException;
}
