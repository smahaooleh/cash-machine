package osmaha.cashmachine.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import osmaha.cashmachine.dto.request.RegistrationRequestDTO;
import osmaha.cashmachine.exception.RegistrationException;
import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.model.Role;
import osmaha.cashmachine.model.RoleE;
import osmaha.cashmachine.model.User;
import osmaha.cashmachine.repository.RoleRepository;
import osmaha.cashmachine.repository.UserRepository;
import osmaha.cashmachine.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = {IllegalArgumentException.class, RegistrationException.class})
    @Override
    public User register(RegistrationRequestDTO userDTO) throws IllegalArgumentException, RegistrationException {

        if (userDTO.getCardNumber() == null || userDTO.getCardNumber().length() != 16 || !isValidCardNumber(userDTO.getCardNumber())) {
            log.warn("IN register - registration for user with card number {} was failed: Card number should have 16 digits", userDTO.getCardNumber());
            throw new IllegalArgumentException("Card number should have 16 digits");
        }
        if (userDTO.getPinCode() == null || userDTO.getPinCode().length() != 4) {
            log.warn("IN register - registration for user with card number {} was failed: PIN code should have 4 digits", userDTO.getCardNumber());
            throw new IllegalArgumentException("PIN code should have 4 digits");
        }

        Role role = roleRepository.findByName(RoleE.USER).orElseThrow(() -> new RuntimeException("User role does not exist in DataBase"));

        Optional<User> checkedExistingUser = userRepository.findByCardNumber(userDTO.getCardNumber());
        if (checkedExistingUser.isPresent()) {
            log.warn("IN register - registration for user with card number {} was failed: Use already exists in DataBase", userDTO.getCardNumber());
            throw new RegistrationException("Use already exists in DataBase");
        }

        User user = new User(userDTO.getCardNumber(), passwordEncoder.encode(userDTO.getPinCode()), 0, LocalDate.now(), new ArrayList<>(), Arrays.asList(role));
        log.info("IN register - registration for user with card number {} was successfully processed", userDTO.getCardNumber());
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        log.info("IN getAll - {} users found", users.size());
        return userRepository.findAll();
    }

    @Override
    public User getByCardNumber(String cardNumber) throws IllegalArgumentException, ResourceNotFoundException {
        if (cardNumber == null || cardNumber.length() != 16 || !isValidCardNumber(cardNumber))
            throw new IllegalArgumentException("Card number should have 16 digits");
        return userRepository.findByCardNumber(cardNumber).orElseThrow(() -> {
            log.warn("IN getByCardNumber - User with card number {} not found", cardNumber);
            return new ResourceNotFoundException("User with card number: " + cardNumber + " not found");
        });
    }

    @Override
    public User getById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("IN getById - User with id {} not found", id);
            return new ResourceNotFoundException("User with id: " + id + " not found");
        });
    }

    private boolean isValidCardNumber(String cardNumber) {
        try {
            Long.parseLong(cardNumber);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
