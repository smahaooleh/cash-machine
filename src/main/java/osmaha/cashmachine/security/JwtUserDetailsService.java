package osmaha.cashmachine.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import osmaha.cashmachine.exception.ResourceNotFoundException;
import osmaha.cashmachine.model.User;
import osmaha.cashmachine.security.jwt.JwtUser;
import osmaha.cashmachine.security.jwt.JwtUserFactory;
import osmaha.cashmachine.service.UserService;

@Service("JwtUserDetailsService")
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String cardNumber) throws UsernameNotFoundException {
        User user;
        try {
            user = userService.getByCardNumber(cardNumber);
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException("User with number of card: " + cardNumber + " not found");
        }
        JwtUser jwtUser = JwtUserFactory.create(user);

        log.info("IN loadUserByUsername - user with number of card: {} successfully loaded", cardNumber);
        return jwtUser;
    }
}
