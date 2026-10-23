package com.inmomarketing.api.controller;

import com.inmomarketing.api.dto.response.InmobiliariaResponse;
import com.inmomarketing.api.dto.response.UserResponse;
import com.inmomarketing.api.service.InmobiliariaService;
import com.inmomarketing.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final InmobiliariaService inmobiliariaService;
    private final UserService userService;

    @GetMapping("/inmobiliarias")
    public ResponseEntity<List<InmobiliariaResponse>> getInmobiliarias() {
        return ResponseEntity.ok(inmobiliariaService.getAll());
    }

    @PatchMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<UserResponse> bloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(userService.bloquearUsuario(id));
    }

    @PatchMapping("/usuarios/{id}/activar")
    public ResponseEntity<UserResponse> activarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activarUsuario(id));
    }
}