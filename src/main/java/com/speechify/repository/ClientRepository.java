package com.speechify.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.speechify.model.Client;
import com.speechify.model.Keys;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public record ClientRepository(ObjectMapper objectMapper, String dbFilePath) implements JSONRepository<Client> {
    static final Keys ID_KEYS = new Keys("clients", "id");

    public CompletableFuture<Optional<Client>> getById(final String id) {
        return getSingle(ID_KEYS, id);
    }

    public CompletableFuture<List<Client>> getAll() {
        return getAll(ID_KEYS, _ -> true);
    }

    @Override
    public Optional<Client> convert(final String id, final ObjectNode node) {
        final var client = new Client(id, node.get("name").asString());
        return Optional.of(client);
    }
}
