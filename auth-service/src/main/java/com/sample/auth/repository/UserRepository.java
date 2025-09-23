package com.sample.auth.repository;

import java.util.Optional;

import com.sample.auth.model.User;

public interface UserRepository {
    void save(User user);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
