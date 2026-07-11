package com.speechify.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ClientTest {

    static Stream<Arguments> clientExceptions() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("blah", null),
                Arguments.of(null, "blah")
        );
    }

    @ParameterizedTest
    @MethodSource("clientExceptions")
    void testExceptions(final String id, final String name) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Client(id, name));
    }
}
