package com.user.nisum.service;

import com.user.nisum.dtos.PhoneDTODTO;
import com.user.nisum.dtos.UserRegistrationRequestDTO;
import com.user.nisum.dtos.UserRegistrationResponseDTO;
import com.user.nisum.entity.Phone;
import com.user.nisum.entity.User;
import com.user.nisum.exception.BusinessRuleException;
import com.user.nisum.exception.ResourceNotFoundException;
import com.user.nisum.mapper.UserMapper;
import com.user.nisum.repository.UserRepository;
import com.user.nisum.service.JwtService;
import com.user.nisum.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequestDTO request;
    private User user;
    private User savedUser;
    private UserRegistrationResponseDTO response;

    @BeforeEach
    void setUp() {
        // Arrange - Datos de prueba (José Francisco Valdez)
        PhoneDTODTO phoneDTO = new PhoneDTODTO();
        phoneDTO.setNumber("1234567");
        phoneDTO.setCitycode("1");
        phoneDTO.setContrycode("57");

        request = new UserRegistrationRequestDTO();
        request.setName("José Francisco Valdez");
        request.setEmail("jose.valdez@empresa.com");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of(phoneDTO));

        user = User.builder()
                .id(UUID.randomUUID())
                .name("José Francisco Valdez")
                .email("jose.valdez@empresa.com")
                .password("encodedPassword")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .build();

        savedUser = User.builder()
                .id(user.getId())
                .name("José Francisco Valdez")
                .email("jose.valdez@empresa.com")
                .password("encodedPassword")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .build();

        response = new UserRegistrationResponseDTO();
        response.setId(user.getId());
        response.setName("José Francisco Valdez");
        response.setEmail("jose.valdez@empresa.com");
        response.setCreated(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setModified(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setLastLogin(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setToken("jwt.token.here");
        response.setIsactive(true);
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("jwt.token.here");
        when(userMapper.toPhoneEntityList(request.getPhones())).thenReturn(List.of(new Phone()));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        // Act
        UserRegistrationResponseDTO result = userService.registerUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("José Francisco Valdez", result.getName());
        assertEquals("jose.valdez@empresa.com", result.getEmail());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode(request.getPassword());
        verify(jwtService).generateToken(user.getId(), user.getEmail());
        verify(userRepository, times(2)).save(any(User.class));
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsBusinessRuleException() {
        // Arrange
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("El correo ya registrado", exception.getMessage());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_MultiplePhones_Success() {
        // Arrange - Múltiples teléfonos (María García)
        PhoneDTODTO phone1 = new PhoneDTODTO();
        phone1.setNumber("9876543");
        phone1.setCitycode("2");
        phone1.setContrycode("57");

        PhoneDTODTO phone2 = new PhoneDTODTO();
        phone2.setNumber("5555555");
        phone2.setCitycode("3");
        phone2.setContrycode("57");

        UserRegistrationRequestDTO multiPhoneRequest = new UserRegistrationRequestDTO();
        multiPhoneRequest.setName("María García");
        multiPhoneRequest.setEmail("maria.garcia@dominio.cl");
        multiPhoneRequest.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        multiPhoneRequest.setPhones(List.of(phone1, phone2));

        User multiPhoneUser = User.builder()
                .id(UUID.randomUUID())
                .name("María García")
                .email("maria.garcia@dominio.cl")
                .password("encodedPassword")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .build();

        UserRegistrationResponseDTO multiPhoneResponse = new UserRegistrationResponseDTO();
        multiPhoneResponse.setId(multiPhoneUser.getId());
        multiPhoneResponse.setName("María García");
        multiPhoneResponse.setEmail("maria.garcia@dominio.cl");
        multiPhoneResponse.setCreated(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        multiPhoneResponse.setModified(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        multiPhoneResponse.setLastLogin(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        multiPhoneResponse.setToken("jwt.token.here");
        multiPhoneResponse.setIsactive(true);
        multiPhoneResponse.setPhones(List.of(phone1, phone2));

        when(userRepository.existsByEmail(multiPhoneRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(multiPhoneRequest)).thenReturn(multiPhoneUser);
        when(passwordEncoder.encode(multiPhoneRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(multiPhoneUser.getId(), multiPhoneUser.getEmail())).thenReturn("jwt.token.here");
        when(userMapper.toPhoneEntityList(multiPhoneRequest.getPhones())).thenReturn(List.of(new Phone(), new Phone()));
        when(userRepository.save(any(User.class))).thenReturn(multiPhoneUser);
        when(userMapper.toResponse(multiPhoneUser)).thenReturn(multiPhoneResponse);

        // Act
        UserRegistrationResponseDTO result = userService.registerUser(multiPhoneRequest);

        // Assert
        assertNotNull(result);
        assertEquals("María García", result.getName());
        assertEquals("maria.garcia@dominio.cl", result.getEmail());
        assertEquals(2, result.getPhones().size());
        assertEquals("9876543", result.getPhones().get(0).getNumber());
        assertEquals("5555555", result.getPhones().get(1).getNumber());
        verify(userRepository).existsByEmail(multiPhoneRequest.getEmail());
        verify(userMapper).toEntity(multiPhoneRequest);
        verify(passwordEncoder).encode(multiPhoneRequest.getPassword());
        verify(jwtService).generateToken(multiPhoneUser.getId(), multiPhoneUser.getEmail());
        verify(userRepository, times(2)).save(any(User.class));
        verify(userMapper).toResponse(multiPhoneUser);
    }

    @Test
    void registerUser_JwtTokenGeneration_Verification() {
        // Arrange
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("jwt.token.here");
        when(userMapper.toPhoneEntityList(request.getPhones())).thenReturn(List.of(new Phone()));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        // Act
        userService.registerUser(request);

        // Assert
        verify(jwtService).generateToken(user.getId(), user.getEmail());
    }

    @Test
    void registerUser_PasswordEncoding_Verification() {
        // Arrange
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("jwt.token.here");
        when(userMapper.toPhoneEntityList(request.getPhones())).thenReturn(List.of(new Phone()));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        // Act
        userService.registerUser(request);

        // Assert
        verify(passwordEncoder).encode("SecurePass1@");
    }

    @Test
    void getUserById_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(savedUser));
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        // Act
        UserRegistrationResponseDTO result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals("José Francisco Valdez", result.getName());
        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void getUserById_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("Usuario no encontrado con ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserByEmail_Success() {
        // Arrange
        String email = "jose.valdez@empresa.com";
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(savedUser));
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        // Act
        UserRegistrationResponseDTO result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals("José Francisco Valdez", result.getName());
        verify(userRepository).findByEmail(email);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void getUserByEmail_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByEmail(email);
        });

        assertEquals("Usuario no encontrado con email: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void login_Success() {
        // Arrange
        String email = "jose.valdez@empresa.com";
        String password = "SecurePass1@";
        String newToken = "new.jwt.token.here";
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(savedUser));
        when(passwordEncoder.matches(password, savedUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(savedUser.getId(), savedUser.getEmail())).thenReturn(newToken);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        // Act
        UserRegistrationResponseDTO result = userService.login(email, password);

        // Assert
        assertNotNull(result);
        assertEquals("José Francisco Valdez", result.getName());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, savedUser.getPassword());
        verify(jwtService).generateToken(savedUser.getId(), savedUser.getEmail());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void login_UserNotFound_ThrowsBusinessRuleException() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "SecurePass1@";
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            userService.login(email, password);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void login_InvalidPassword_ThrowsBusinessRuleException() {
        // Arrange
        String email = "jose.valdez@empresa.com";
        String password = "WrongPassword";
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(savedUser));
        when(passwordEncoder.matches(password, savedUser.getPassword())).thenReturn(false);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            userService.login(email, password);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, savedUser.getPassword());
        verify(jwtService, never()).generateToken(any(), any());
        verify(userRepository, never()).save(any());
    }
} 