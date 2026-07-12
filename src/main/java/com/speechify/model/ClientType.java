package com.speechify.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableMap;

// TODO: should probably be another lookup
public enum ClientType {
    VERY_IMPORTANT_CLIENT("VeryImportantClient", false, 0),
    IMPORTANT_CLIENT("ImportantClient", true, 20000),
    DEFAULT(true, 10000)
    ;

    final String specialName;
    final boolean hasLimit;
    final double limitAmount; // TODO: using double as money is naughty, need to fix

    ClientType(final String specialName, final boolean hasLimit, final double limitAmount) {
        this.specialName = specialName;
        this.hasLimit = hasLimit;
        this.limitAmount = limitAmount;
    }

    ClientType(final boolean hasLimit, final double limitAmount) {
        this.specialName = null;
        this.hasLimit = hasLimit;
        this.limitAmount = limitAmount;
    }

    public static final Map<String, ClientType> CLIENT_NAME_MAP = EnumSet.allOf(ClientType.class).stream()
            .filter(value -> value.specialName != null)
            .collect(toUnmodifiableMap(type -> type.specialName, Function.identity()));


    public String getSpecialName() {
        return specialName;
    }


    public boolean hasLimit() {
        return hasLimit;
    }

    public double getLimitAmount() {
        return limitAmount;
    }
}
