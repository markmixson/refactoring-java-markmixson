package com.speechify.repository;

import com.speechify.TestUtil;
import com.speechify.model.Client;
import com.speechify.model.ClientType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

class ClientRepositoryTest {

    private ClientRepository clientRepository;

    @BeforeEach
    void setup() throws IOException {
        clientRepository = new ClientRepository(TestUtil.OBJECT_MAPPER, TestUtil.getTempFile());
    }

    @Test
    void testGetAll() {
        final var clients = clientRepository.getAll().join();
        Assertions.assertEquals(3, clients.size());
        Assertions.assertTrue(clients.stream().anyMatch(client ->
                client.name().equals(ClientType.IMPORTANT_CLIENT.getSpecialName())));
        Assertions.assertTrue(clients.stream().anyMatch(client ->
                client.name().equals(ClientType.VERY_IMPORTANT_CLIENT.getSpecialName())));
    }

    @Test
    void testGetById() {
        final var clients = clientRepository.getAll().join();
        final var clientsFromGetById = clients.stream()
                .map(Client::id)
                .map(id -> clientRepository.getById(id).join())
                .flatMap(Optional::stream)
                .toList();
        for (int i = 0; i < clients.size(); i++) {
            Assertions.assertEquals(clients.get(i), clientsFromGetById.get(i));
        }
    }

    @Test
    void testGetById_noValues() {
        Assertions.assertTrue(clientRepository.getById("blah")
                .join()
                .isEmpty());
    }

    @Test
    void testGetAll_throwsExceptionOnBadFilter() {
        clientRepository = new ClientRepository(new ObjectMapper(), "meh");
        Assertions.assertEquals(List.of(),
                clientRepository.getAll().join());
    }

    @Test
    void testAdd() throws IOException {
        Assertions.assertNotNull(clientRepository.add(
                ClientRepository.ID_KEYS, new Client("123", "hello")));
        final var output = clientRepository.getById("123").join().orElseThrow();
        Assertions.assertEquals("123", output.id());
    }
}
