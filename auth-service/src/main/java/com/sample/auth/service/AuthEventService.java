package com.sample.auth.service;

import com.sample.auth.model.User;
import com.sample.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthEventService {

    private static final Logger logger = LoggerFactory.getLogger(AuthEventService.class);
    private final UserRepository userRepository;

    public AuthEventService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean loginUser(User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.username());
        if (foundUser.isPresent() && foundUser.get().password().equals(user.password())) {
            logger.info("Login successful for user: {}", user.username());
            return true;
        } else {
            logger.warn("Login failed for user: {}. Invalid credentials or user not found.", user.username());
            return false;
        }
    }

    public boolean registerUser(User user) {
        if (userRepository.existsByUsername(user.username())) {
            logger.warn("Registration failed: Username {} already exists.", user.username());
            return false;
        } else {
            userRepository.save(user);
            logger.info("User {} registered successfully.", user.username());
            return true;
        }
    }

    public void processLoginEvent(User user) {
        logger.info("Async Event: Processing login attempt for user '{}'", user.username());
    }

    public void processRegisterEvent(User user) {
        logger.info("Async Event: Processing registration attempt for user '{}'", user.username());
    }
}
