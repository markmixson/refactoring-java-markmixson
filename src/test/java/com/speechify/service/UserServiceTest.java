package com.speechify.service;

import com.speechify.TestUtil;
import com.speechify.cache.CacheLimits;
import com.speechify.model.ClientType;
import com.speechify.model.User;
import com.speechify.model.UserDetails;
import com.speechify.repository.ClientRepository;
import com.speechify.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

class UserServiceTest {
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-07-11T12:17:17Z"), ZoneId.of("UTC"));
    private UserService userService;
    private ClientRepository clientRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setup() throws IOException {
        final var tmpFile = TestUtil.getTempFile();
        userRepository = new UserRepository(TestUtil.OBJECT_MAPPER, tmpFile);
        clientRepository = new ClientRepository(TestUtil.OBJECT_MAPPER, tmpFile);
        userService = new UserService(clientRepository, userRepository, FIXED_CLOCK, new CacheLimits(10));
    }

    @Test
    void testAddUser() {
        final var client = clientRepository.getAll()
                .join()
                .stream()
                .findFirst()
                .orElseThrow();
        final var uuid = UUID.randomUUID().toString();
        final var user = addUser(uuid, client.id());
        Assertions.assertEquals(uuid, user.details().firstname());
        Assertions.assertEquals(uuid, user.details().email());
        Assertions.assertEquals(uuid, user.details().surname());
        Assertions.assertEquals(LocalDate.EPOCH, user.details().dateOfBirth());
        Assertions.assertEquals(client.id(), user.client().id());

        final var clientType = ClientType.CLIENT_NAME_MAP.get(client.name());
        Assertions.assertEquals(clientType.getSpecialName(), user.client().name());
        Assertions.assertEquals(clientType.hasLimit(), user.hasCreditLimit());
        Assertions.assertEquals(clientType.getLimitAmount(), user.creditLimit());
    }

    private User addUser(final String uuid, final String clientId) {
        Assertions.assertTrue(
                userService.addUser(uuid, uuid, uuid, LocalDate.EPOCH, clientId)
                        .join());
        return userRepository.getAll()
                .join()
                .stream()
                .filter(myUser -> myUser.details().firstname().equals(uuid))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void testAddUser_fail() {
        Assertions.assertFalse(
                userService.addUser(null, null, null, null, null)
                        .join());
    }

    @Test
    void testAddUser_alreadyExists() {
        final var user = userRepository.getAll()
                .join()
                .stream()
                .findFirst()
                .orElseThrow();
        Assertions.assertFalse(
                userService.addUser(user.details().firstname(),
                                user.details().surname(),
                                user.details().email(),
                                user.details().dateOfBirth(),
                                user.client().id())
                        .join());
    }

    @Test
    void testAddUser_clientDoesNotExist() {
        final var user = userRepository.getAll()
                .join()
                .stream()
                .findFirst()
                .orElseThrow();
        Assertions.assertFalse(
                userService.addUser(user.details().firstname(),
                                user.details().surname(),
                                "meh",
                                user.details().dateOfBirth(),
                                "blah")
                        .join());
    }

    @Test
    void testUpdateUser() {
        final var newLastName = "something";
        final var client = clientRepository.getAll()
                .join()
                .stream()
                .findFirst()
                .orElseThrow();
        final var uuid = UUID.randomUUID().toString();
        final var user = addUser(uuid, client.id());
        final var updatedUser = new User(user.id(), user.client(),
                new UserDetails(
                        user.details().dateOfBirth(),
                        user.details().email(),
                        user.details().firstname(),
                        newLastName,
                        FIXED_CLOCK.instant()),
                user.hasCreditLimit(),
                user.creditLimit());
        Assertions.assertTrue(userService.updateUser(updatedUser).join());
        final var checkedUser = userRepository.getSingle(UserRepository.ID_KEYS, user.id()).join().orElseThrow();
        Assertions.assertEquals(newLastName, checkedUser.details().surname());
    }

    @Test
    void testUpdateUser_null() {
        Assertions.assertFalse(userService.updateUser(null).join());
    }

    @Test
    void testUpdateUser_notPresent() {
        Assertions.assertFalse(userService.updateUser(TestUtil.getUser(UUID.randomUUID().toString())).join());
    }

    @Test
    void testGetAll() {
        final var users = userService.getAllUsers().join();
        Assertions.assertEquals(2, users.size());
        Assertions.assertTrue(users.stream().anyMatch(user ->
                user.details().firstname().equals("John")));
        Assertions.assertTrue(users.stream().anyMatch(client ->
                client.details().firstname().equals("Jane")));
    }

    @Test
    void testGetByEmail() {
        final var client = clientRepository.getAll()
                .join()
                .stream()
                .findFirst()
                .orElseThrow();
        final var uuid = UUID.randomUUID().toString();
        final var user = addUser(uuid, client.id());
        final var emailLookupUser = userService.getUserByEmail(user.details().email()).join().orElseThrow();
        Assertions.assertEquals(emailLookupUser.id(), user.id());
    }
}
