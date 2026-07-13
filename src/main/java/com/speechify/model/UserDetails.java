package com.speechify.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public record UserDetails(
        LocalDate dateOfBirth,
        String email,
        String firstname,
        String surname) {
    private static final ZoneId UTC = ZoneId.of("UTC");

    public void checkOnAdd(final Instant currentTime) throws IllegalStateException {
        if (firstname == null
                || surname == null
                || email == null
                || dateOfBirth == null
                || currentTime == null
                || Period.between(dateOfBirth, currentTime.atZone(UTC).toLocalDate()).getYears() < 21) {
            throw new IllegalStateException();
        }
    }
}
