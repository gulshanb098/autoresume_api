package com.autoresume.autoresume_api.repository;

import com.autoresume.autoresume_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByEmail(String email);
}