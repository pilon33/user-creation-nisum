package com.user.nisum.service;

import com.user.nisum.dtos.UserRegistrationRequestDTO;
import com.user.nisum.dtos.UserRegistrationResponseDTO;

import java.util.UUID;

public interface UserService {
    UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO request);
    UserRegistrationResponseDTO getUserById(UUID id);
    UserRegistrationResponseDTO getUserByEmail(String email);
    UserRegistrationResponseDTO updateLastLogin(UUID userId);
    UserRegistrationResponseDTO login(String email, String password);
} 