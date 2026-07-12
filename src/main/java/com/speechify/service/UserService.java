package com.speechify.service;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.speechify.model.User;
import com.speechify.model.UserDetails;
import com.speechify.repository.ClientRepository;
import com.speechify.repository.UserRepository;

public class UserService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public UserService(final ClientRepository clientRepository,
                       final UserRepository userRepository,
                       final Clock clock) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.clock = clock;
    }

    @SuppressWarnings("java:S106") // TODO: setup proper logger
    public CompletableFuture<Boolean> addUser(
            final String firstname,
            final String surname,
            final String email,
            final LocalDate dateOfBirth,
            final String clientId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final var details = new UserDetails(dateOfBirth, email, firstname, surname, clock.instant());
                details.check();
                return userRepository.add(UserRepository.ID_KEYS, () -> buildUser(details, clientId));
            } catch (IllegalStateException | IllegalArgumentException | IOException e) {
                System.err.printf("User not added: %s%n", e.getMessage());
                return false;
            }
        });
    }

    @SuppressWarnings("java:S106") // TODO: setup proper logger
    private User buildUser(final UserDetails details,
                           final String clientId) throws IllegalArgumentException {
        if (getUserByEmail(details.email())
                .join()
                .isPresent()) {
            throw new IllegalArgumentException("User is already present");
        }
        final var client = clientRepository.getById(clientId)
                .join()
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        return User.build(details, client);
    }

    @SuppressWarnings("java:S106") // TODO: setup proper logger
    public CompletableFuture<Boolean> updateUser(final User user) {
        return CompletableFuture.supplyAsync(() -> {
            if (user == null) {
                System.err.println("User not added because user is empty!");
                return false;
            } else {
                return userRepository.update(user);
            }
        });
    }

    public CompletableFuture<List<User>> getAllUsers() {
        return userRepository.getAll();
    }

    public CompletableFuture<Optional<User>> getUserByEmail(String email) {
        return userRepository.getByEmail(email);
    }
}
