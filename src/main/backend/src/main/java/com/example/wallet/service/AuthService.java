package com.example.wallet.service;

import com.example.wallet.conf.JwtConfig;
import com.example.wallet.entity.AppUser;
import com.example.wallet.exception.AuthenticationException;
import com.example.wallet.exception.RegistrationException;
import com.example.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    public String login(String username, String password) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }
        return jwtConfig.generateToken(username);
    }

    public void register(String username, String password) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            throw new RegistrationException("Username must be between 3 and 50 characters.");
        }

        if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new RegistrationException("Password must be at least 8 characters and contain a lowercase letter, an uppercase letter, and a number.");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RegistrationException("Username already exists.");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(appUser);
    }

}
