package com.speechify.repository;

import com.speechify.model.Keys;
import com.speechify.model.User;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record UserRepository(ObjectMapper objectMapper, String dbFilePath) implements JSONRepository<User> {
    public static final Keys ID_KEYS = new Keys("users", "id");
    public static final Keys EMAIL_KEYS = new Keys("users", "email");

    public CompletableFuture<Optional<User>> getByEmail(final String email) {
        return getSingle(EMAIL_KEYS, email);
    }

    public CompletableFuture<List<User>> getAll() {
        return getAll(ID_KEYS, _ -> true);
    }

    @Override
    public Optional<User> convert(final String id, final ObjectNode node) {
            return Optional.of(objectMapper.treeToValue(node, User.class));
    }

    @SuppressWarnings("java:S106") // TODO: setup proper logger
    public boolean update(final User user) {
        try {
            return update(ID_KEYS, () -> user, user::id);
        } catch (IOException e) {
            System.err.printf("Error updating user: %s%n", e.getMessage());
            return false;
        }
    }
}
