package com.user.nisum.controller;

import com.user.nisum.controllers.resources.UsuariosResource;
import com.user.nisum.dtos.LoginRequestDTO;
import com.user.nisum.dtos.LoginResponseDTO;
import com.user.nisum.dtos.UserRegistrationRequestDTO;
import com.user.nisum.dtos.UserRegistrationResponseDTO;
import com.user.nisum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Map;

import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController implements UsuariosResource {

    private final UserService userService;
    private final NativeWebRequest request;

    @Autowired
    public UserController(UserService userService, Optional<NativeWebRequest> request) {
        this.userService = userService;
        this.request = request.orElse(null);
    }

    /**
     * Proporciona acceso seguro al objeto NativeWebRequest actual.
     *
     * Este método permite obtener información adicional de la petición HTTP como:
     * - Headers (User-Agent, Authorization, etc.)
     * - IP del cliente
     * - Parámetros de la URL
     * - Atributos de la sesión
     *
     * Se implementa porque es requerido por la interfaz UsuariosResource
     * y permite acceso controlado a detalles de la petición cuando sea necesario.
     *
     * @return Optional que contiene el NativeWebRequest si está disponible,
     *         o Optional.empty() si no hay petición activa
     */

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public UserRegistrationResponseDTO registrarUsuario(UserRegistrationRequestDTO request) {
        return userService.registerUser(request);
    }

    @Override
    public UserRegistrationResponseDTO obtenerUsuarioPorEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public UserRegistrationResponseDTO actualizarUltimoLogin(UUID id) {
        return userService.updateLastLogin(id);
    }

    /**
     * Endpoint protegido para verificar que el token JWT es válido
     * Requiere un token JWT válido en el header Authorization
     */
    @GetMapping("/api/usuarios/verificar-token")
    public ResponseEntity<Map<String, Object>> verificarToken() {
        Map<String, Object> response = Map.of(
            "mensaje", "Token JWT válido",
            "status", "authenticated",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(response);
    }

    @Override
    public LoginResponseDTO apiUsuariosLoginPost(LoginRequestDTO loginRequestDTO) {
        UserRegistrationResponseDTO user = userService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        
        LoginResponseDTO response = new LoginResponseDTO();
        response.setMensaje("Login exitoso");
        response.setToken(user.getToken());
        response.setUsuario(user);
        
        return response;
    }
} 