package com.speechify.repository;

import com.speechify.model.Keys;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface JSONRepository<T> {
    default CompletableFuture<Optional<T>> getSingle(
            final Keys keys,
            final String id) {
        final var future = getAll(keys, id::equals);
        return future.thenApply(list -> {
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(list.getFirst());
            }
        });
    }

    default CompletableFuture<List<T>> getAll(
            final Keys keys,
            final Predicate<String> idFilter) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return doGetAll(keys, idFilter);
            } catch (IOException _) {
                return List.of();
            }
        });
    }

    private List<T> doGetAll(final Keys keys, final Predicate<String> idFilter) throws IOException {
        final var list = new ArrayList<T>();
        final var dbFile = getDbFile();
        final var root = getRoot(dbFile);
        final var items = getItems(root, keys);
        for (int i = 0; i < items.size(); i++) {
            final var node = (ObjectNode) items.get(i);
            final var id = node.get(keys.fieldKey()).asString();
            if (idFilter.test(id)) {
                convert(id, node).ifPresent(list::add);
            }
        }
        return List.copyOf(list);
    }

    private File getDbFile() throws IOException {
        File dbFile = new File(dbFilePath());
        if (!dbFile.exists()) {
            throw new IOException("db file missing!");
        }
        return dbFile;
    }

    private ObjectNode getRoot(final File dbFile) {
        return (ObjectNode) objectMapper().readTree(dbFile);
    }

    private ArrayNode getItems(final ObjectNode root, final Keys keys) {
        return (ArrayNode) root.get(keys.itemKey());
    }

    default boolean add(final Keys keys, final Supplier<T> supplier) throws IOException {
        final var dbFile = getDbFile();
        final var root = getRoot(dbFile);
        final var items = getItems(root, keys);
        items.add(objectMapper().valueToTree(supplier.get()));
        objectMapper().writeValue(dbFile, root);
        return true;
    }

    default boolean update(final Keys keys,
                           final Supplier<T> itemSupplier,
                           final Supplier<String> idSupplier) throws IOException {
        final var dbFile = getDbFile();
        final var root = getRoot(dbFile);
        final var items = getItems(root, keys);
        for (int i = 0; i < items.size(); i++) {
            final var node = (ObjectNode) items.get(i);
            if (node.get(keys.fieldKey()).asString().equals(idSupplier.get())) {
                items.set(i, objectMapper().valueToTree(itemSupplier.get()));
                objectMapper().writeValue(dbFile, root);
                return true;
            }
        }
        return false;
    }

    String dbFilePath();

    ObjectMapper objectMapper();

    Optional<T> convert(String id, ObjectNode node);
}
