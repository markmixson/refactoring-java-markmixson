package com.speechify.model;

public record Client(
        String id,
        String name) {
    public Client {
        if (id == null || name == null) {
            throw new IllegalArgumentException();
        }
    }
}
