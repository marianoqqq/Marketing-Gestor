package com.example.demo.securityJwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String errorMessage = switch (authException) {
            case BadCredentialsException e -> "Credenciales inválidas";
            case DisabledException e -> "Cuenta deshabilitada";
            case LockedException e -> "Cuenta bloqueada";
            case AccountExpiredException e -> "Cuenta expirada";
            case CredentialsExpiredException e -> "Credenciales expiradas";
            case InsufficientAuthenticationException e -> "Estado de autenticación insuficiente (Falta token de acceso)";
            case AuthenticationServiceException e -> "Error del servicio de autenticación";
            default -> "Error de autenticación: " + authException.getMessage();
        };
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.UNAUTHORIZED.value());
        errorBody.put("message", errorMessage);
        errorBody.put("path", request.getRequestURI());
        errorBody.put("timestamp", LocalDateTime.now());
        String jsonResponse = objectMapper.writeValueAsString(errorBody);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}