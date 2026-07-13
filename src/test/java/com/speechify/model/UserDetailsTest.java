package com.speechify.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

class UserDetailsTest {

    private static final LocalDate DEFAULT_BIRTHDAY = LocalDate.EPOCH;
    private static final Instant DEFAULT_TIME = Instant.MAX;

    static Stream<Arguments> nullExceptions() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of("i", null, null),
                Arguments.of(null, "i", null),
                Arguments.of(null, null, "i"),
                Arguments.of("i", "i", null),
                Arguments.of(null, "i", "i"),
                Arguments.of("i", null, "i")
        );
    }

    @ParameterizedTest
    @MethodSource("nullExceptions")
    void testNullValueExceptions(final String email, final String firstName, final String surname) {
        final var toTest = new UserDetails(DEFAULT_BIRTHDAY, email, firstName, surname);
        Assertions.assertThrows(IllegalStateException.class, () -> toTest.checkOnAdd(DEFAULT_TIME));
    }

    static Stream<Arguments> birthdayExceptions() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, Instant.EPOCH),
                Arguments.of(LocalDate.EPOCH, null),
                Arguments.of(LocalDate.EPOCH, Instant.EPOCH),
                Arguments.of(LocalDate.MAX, Instant.EPOCH),
                Arguments.of(LocalDate.EPOCH,
                        Instant.EPOCH.atZone(ZoneId.of("UTC")).plusYears(20).toInstant())
        );
    }

    @ParameterizedTest
    @MethodSource("birthdayExceptions")
    void testBirthDateExceptions(final LocalDate localDate, final Instant currentTime) {
        final var toTest = new UserDetails(localDate, "hi", "hi", "hi");
        Assertions.assertThrows(IllegalStateException.class, () -> toTest.checkOnAdd(currentTime));
    }
}
