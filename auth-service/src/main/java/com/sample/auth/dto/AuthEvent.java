package com.sample.auth.dto;

public record AuthEvent(
    String username,
    String password
) {}
