package com.user.nisum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.nisum.dtos.PhoneDTODTO;
import com.user.nisum.dtos.UserRegistrationRequestDTO;
import com.user.nisum.dtos.UserRegistrationResponseDTO;
import com.user.nisum.exception.BusinessRuleException;
import com.user.nisum.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser_Success() throws Exception {
        // Arrange - Registro exitoso (José Francisco Valdez)
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("José Francisco Valdez");
        request.setEmail("jose.valdez@empresa.com");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of(phone));

        UserRegistrationResponseDTO response = new UserRegistrationResponseDTO();
        response.setId(UUID.randomUUID());
        response.setName("José Francisco Valdez");
        response.setEmail("jose.valdez@empresa.com");
        response.setCreated(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setModified(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setLastLogin(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setToken("jwt.token.here");
        response.setIsactive(true);
        response.setPhones(List.of(phone));

        when(userService.registerUser(any(UserRegistrationRequestDTO.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("José Francisco Valdez"))
                .andExpect(jsonPath("$.email").value("jose.valdez@empresa.com"))
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.phones").isArray())
                .andExpect(jsonPath("$.phones").value(org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.phones[0].number").value("1234567"))
                .andExpect(jsonPath("$.phones[0].citycode").value("1"));
    }

    @Test
    void registerUser_DuplicateEmail_ReturnsConflict() throws Exception {
        // Arrange - Email duplicado
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("José Francisco Valdez");
        request.setEmail("jose.valdez@empresa.com");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of(phone));

        when(userService.registerUser(any(UserRegistrationRequestDTO.class)))
                .thenThrow(new BusinessRuleException("El correo ya registrado"));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El correo ya registrado"));
    }

    @Test
    void registerUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange - Email inválido (Chavela Primera)
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("Chavela Primera");
        request.setEmail("invalid-email");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of(phone));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WeakPassword_ReturnsBadRequest() throws Exception {
        // Arrange - Contraseña débil (Carlos Mendoza)
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("Carlos Mendoza");
        request.setEmail("carlos.mendoza@test.org");
        request.setPassword("weak"); // Contraseña que NO cumple con el patrón
        request.setPhones(List.of(phone));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_MultiplePhones_Success() throws Exception {
        // Arrange - Múltiples teléfonos (María García)
        PhoneDTODTO phone1 = new PhoneDTODTO();
        phone1.setNumber("9876543");
        phone1.setCitycode("2");
        phone1.setContrycode("57");

        PhoneDTODTO phone2 = new PhoneDTODTO();
        phone2.setNumber("5555555");
        phone2.setCitycode("3");
        phone2.setContrycode("57");

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("María García");
        request.setEmail("maria.garcia@dominio.cl");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of(phone1, phone2));

        UserRegistrationResponseDTO response = new UserRegistrationResponseDTO();
        response.setId(UUID.randomUUID());
        response.setName("María García");
        response.setEmail("maria.garcia@dominio.cl");
        response.setCreated(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setModified(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setLastLogin(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setToken("jwt.token.here");
        response.setIsactive(true);
        response.setPhones(List.of(phone1, phone2));

        when(userService.registerUser(any(UserRegistrationRequestDTO.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("María García"))
                .andExpect(jsonPath("$.email").value("maria.garcia@dominio.cl"))
                .andExpect(jsonPath("$.phones").isArray())
                .andExpect(jsonPath("$.phones").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.phones[0].number").value("9876543"))
                .andExpect(jsonPath("$.phones[0].citycode").value("2"))
                .andExpect(jsonPath("$.phones[1].number").value("5555555"))
                .andExpect(jsonPath("$.phones[1].citycode").value("3"));
    }

    @Test
    void registerUser_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange - Nombre vacío
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("");
        request.setEmail("test@example.com");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of(phone));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmptyPhones_ReturnsBadRequest() throws Exception {
        // Arrange - Lista de teléfonos vacía
        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of());

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidPhoneNumber_ReturnsBadRequest() throws Exception {
        // Arrange - Número de teléfono inválido
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber(""); // Número vacío
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("SecurePass1@"); // Contraseña que cumple con el patrón
        request.setPhones(List.of(phone));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserByEmail_Success() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationResponseDTO response = new UserRegistrationResponseDTO();
        response.setId(userId);
        response.setName("José Francisco Valdez");
        response.setEmail("jose.valdez@example.com");
        response.setCreated(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setModified(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setLastLogin(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setToken("jwt.token.here");
        response.setIsactive(true);
        response.setPhones(List.of(phone));

        when(userService.getUserByEmail("jose.valdez@example.com"))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/email/jose.valdez@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("José Francisco Valdez"))
                .andExpect(jsonPath("$.email").value("jose.valdez@example.com"))
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.last_login").exists())
                .andExpect(jsonPath("$.token").value("jwt.token.here"));
    }

    @Test
    void updateLastLogin_Success() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        PhoneDTODTO phone = new PhoneDTODTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setContrycode("57");

        UserRegistrationResponseDTO response = new UserRegistrationResponseDTO();
        response.setId(userId);
        response.setName("José Francisco Valdez");
        response.setEmail("jose.valdez@example.com");
        response.setCreated(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setModified(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setLastLogin(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        response.setToken("jwt.token.here");
        response.setIsactive(true);
        response.setPhones(List.of(phone));

        when(userService.updateLastLogin(userId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/usuarios/" + userId + "/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("José Francisco Valdez"))
                .andExpect(jsonPath("$.email").value("jose.valdez@example.com"))
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.last_login").exists())
                .andExpect(jsonPath("$.token").value("jwt.token.here"));
    }
} 