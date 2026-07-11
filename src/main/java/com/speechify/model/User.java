package com.speechify.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.UUID;


public record User(
        String id,
        Client client,
        @JsonUnwrapped
        UserDetails details,
        Boolean hasCreditLimit,
        Double creditLimit) { // TODO: using money as a double is naughty - need to replace this
    public static User build(final UserDetails details,
                             final Client client) {
        final var id = UUID.randomUUID().toString();
        final ClientType clientType = ClientType.CLIENT_NAME_MAP.getOrDefault(client.name(), ClientType.DEFAULT);
        return new User(id, client, details, clientType.hasLimit, clientType.limitAmount);
    }
}
