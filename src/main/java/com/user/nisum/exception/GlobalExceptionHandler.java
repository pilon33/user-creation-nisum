package com.user.nisum.exception;

import com.user.nisum.dtos.ErrorDTODTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTODTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorDTODTO error = new ErrorDTODTO();
        error.setMensaje(errorMessage);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorDTODTO> handleBusinessRuleException(BusinessRuleException ex) {
        ErrorDTODTO error = new ErrorDTODTO();
        error.setMensaje(ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTODTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorDTODTO error = new ErrorDTODTO();
        error.setMensaje(ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTODTO> handleGenericException(Exception ex) {
        ErrorDTODTO error = new ErrorDTODTO();
        error.setMensaje("Error interno del servidor");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
} 