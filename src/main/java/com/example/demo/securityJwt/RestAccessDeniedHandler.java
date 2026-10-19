package com.example.demo.securityJwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", HttpStatus.FORBIDDEN.value());
        errorBody.put("message", "Acceso denegado: No tienes permisos suficientes para realizar esta acción.");
        errorBody.put("path", request.getRequestURI());
        errorBody.put("timestamp", LocalDateTime.now());

        String jsonResponse = objectMapper.writeValueAsString(errorBody);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
