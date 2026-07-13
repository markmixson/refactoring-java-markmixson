package com.speechify.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class UserTest {

    private static final UserDetails TEST_DETAILS =
            new UserDetails(LocalDate.EPOCH, "hi@hi.com", "meh", "meh");

    static Stream<Arguments> creditArguments() {
        return Stream.of(
                Arguments.of(User.build(TEST_DETAILS,
                        new Client("id", "bah")), true, 10000),
                Arguments.of(User.build(
                        TEST_DETAILS,
                        new Client("id", "VeryImportantClient")), false, 0),
                Arguments.of(User.build(
                        TEST_DETAILS,
                        new Client("id", "ImportantClient")), true, 20000)
        );
    }

    @ParameterizedTest
    @MethodSource("creditArguments")
    void testCredit(final User user, final boolean hasLimit, final double creditAmount) {
        Assertions.assertEquals(hasLimit, user.hasCreditLimit());
        Assertions.assertEquals(creditAmount, user.creditLimit());
    }
}
