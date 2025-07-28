package com.user.nisum.service.impl;

import com.user.nisum.dtos.UserRegistrationRequestDTO;
import com.user.nisum.dtos.UserRegistrationResponseDTO;
import com.user.nisum.entity.Phone;
import com.user.nisum.entity.User;
import com.user.nisum.exception.BusinessRuleException;
import com.user.nisum.exception.ResourceNotFoundException;
import com.user.nisum.mapper.UserMapper;
import com.user.nisum.repository.UserRepository;
import com.user.nisum.service.JwtService;
import com.user.nisum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                          UserMapper userMapper,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El correo ya registrado");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        List<Phone> phones = userMapper.toPhoneEntityList(request.getPhones());
        phones.forEach(phone -> phone.setUser(user));
        user.setPhones(phones);

        User savedUser = userRepository.save(user);
        
        // Generar token después de guardar el usuario para tener el ID
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());
        savedUser.setToken(token);
        
        // Guardar el usuario con el token
        User finalUser = userRepository.save(savedUser);
        return userMapper.toResponse(finalUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRegistrationResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRegistrationResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserRegistrationResponseDTO updateLastLogin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        
        user.setLastLogin(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        return userMapper.toResponse(updatedUser);
    }
} 