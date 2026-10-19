package com.inmomarketing.api.controller;

import com.inmomarketing.api.dto.request.InmobiliariaRequest;
import com.inmomarketing.api.dto.response.InmobiliariaResponse;
import com.inmomarketing.api.service.InmobiliariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/inmobiliaria")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class InmobiliariaController {
    private final InmobiliariaService inmobiliariaService;

    @GetMapping
    public ResponseEntity<InmobiliariaResponse> getMiInmobiliaria(Principal principal) {
        return ResponseEntity.ok(inmobiliariaService.getMiInmobiliaria(principal.getName()));
    }

    @PutMapping
    public ResponseEntity<InmobiliariaResponse> update(@Valid @RequestBody InmobiliariaRequest request,
                                                       Principal principal) {
        return ResponseEntity.ok(inmobiliariaService.update(request, principal.getName()));
    }
}