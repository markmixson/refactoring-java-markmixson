package com.speechify.repository;

import com.speechify.TestUtil;
import com.speechify.model.Client;
import com.speechify.model.User;
import com.speechify.model.UserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

class UserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setup() throws IOException {
        userRepository = new UserRepository(TestUtil.OBJECT_MAPPER, TestUtil.getTempFile());
    }

    @Test
    void testGetByEmail() {
        Assertions.assertEquals("John",
                userRepository.getByEmail("john.doe@example.com")
                        .join()
                        .orElseThrow()
                        .details().firstname());
    }

    @Test
    void testGetAll() {
        final var users = userRepository.getAll().join();
        Assertions.assertEquals(2, users.size());
        Assertions.assertTrue(users.stream().anyMatch(user ->
                user.details().firstname().equals("John")));
        Assertions.assertTrue(users.stream().anyMatch(client ->
                client.details().firstname().equals("Jane")));
    }

    @Test
    void testUpdate() throws IOException {
        final var id = UUID.randomUUID().toString();
        final var output = userRepository.add(
                UserRepository.ID_KEYS, TestUtil.getUser(id));
        Assertions.assertEquals(id, output.id());
        final var updated = getUpdated(id);
        Assertions.assertTrue(userRepository.update(updated));
        final var outputUpdated = userRepository.getSingle(UserRepository.ID_KEYS, id).join().orElseThrow();
        Assertions.assertEquals(updated, outputUpdated);
    }

    private User getUpdated(final String id) {
        return new User(id, new Client("good", "good"), new UserDetails(
                LocalDate.MAX,
                "hiii",
                "hiii",
                "hiii",
                Instant.MAX),
                false,
                0.0);
    }

    @Test
    void testUpdate_missingValue() {
        final var id = UUID.randomUUID().toString();
        final var updated = getUpdated(id);
        Assertions.assertFalse(userRepository.update(updated));
        Assertions.assertTrue(userRepository.getSingle(UserRepository.ID_KEYS, id).join().isEmpty());
    }

    @Test
    void testUpdate_ioException() {
        userRepository = new UserRepository(TestUtil.OBJECT_MAPPER, "blah");
        final var id = UUID.randomUUID().toString();
        final var updated = getUpdated(id);
        Assertions.assertFalse(userRepository.update(updated));
    }
}
