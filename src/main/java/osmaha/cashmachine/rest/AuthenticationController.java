package osmaha.cashmachine.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import osmaha.cashmachine.dto.request.AuthenticationRequestDTO;
import osmaha.cashmachine.dto.request.RegistrationRequestDTO;
import osmaha.cashmachine.dto.response.AuthenticationResponseDTO;
import osmaha.cashmachine.dto.response.MessageResponseDTO;
import osmaha.cashmachine.exception.RegistrationException;
import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.model.User;
import osmaha.cashmachine.security.jwt.JwtTokenProvider;
import osmaha.cashmachine.service.UserService;

@RestController
@RequestMapping(value = "/api/v1/auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDTO requestDTO) {
        try {
            if(requestDTO.getCardNumber() == null || requestDTO.getPinCode() == null) throw new IllegalArgumentException("Invalid username or password");
            String cardNumber = requestDTO.getCardNumber();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(cardNumber, requestDTO.getPinCode()));

            User user = userService.getByCardNumber(cardNumber);

            String token = jwtTokenProvider.createToken(user.getCardNumber());

            AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(cardNumber, token);
            log.info("IN login - authentication request with card number {} was successfully processed", requestDTO.getCardNumber());
            return ResponseEntity.ok(responseDTO);

        } catch (AuthenticationException | ResourceNotFoundException | IllegalArgumentException e) {
            log.warn("IN login - authentication request with card number {} was failed", requestDTO.getCardNumber());
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody RegistrationRequestDTO requestDTO) {
        try {
            User user = userService.register(requestDTO);
            log.info("IN register - user with card number {} was successfully registered", requestDTO.getCardNumber());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException | RegistrationException e) {
            log.warn("IN register - user with card number {} was not registered due to exception {}: " + e.getMessage(), requestDTO.getCardNumber(), e.getClass().getSimpleName());
            return new ResponseEntity<>(new MessageResponseDTO("Registration was failed: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
