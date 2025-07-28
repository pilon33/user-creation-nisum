package com.user.nisum.service;

import java.util.UUID;

public interface JwtService {
    String generateToken(UUID userId, String email);
    String extractUserId(String token);
    String extractEmail(String token);
    boolean isTokenValid(String token);
} 