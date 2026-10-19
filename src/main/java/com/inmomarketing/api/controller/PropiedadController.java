package com.inmomarketing.api.controller;

import com.inmomarketing.api.dto.request.PropiedadRequest;
import com.inmomarketing.api.dto.response.PropiedadResponse;
import com.inmomarketing.api.service.PropiedadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PropiedadController {
    private final PropiedadService propiedadService;

    @PostMapping
    public ResponseEntity<PropiedadResponse> create(@Valid @RequestBody PropiedadRequest request,  Principal principal) {
        return new ResponseEntity<>(propiedadService.create(request, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PropiedadResponse>> getAll(Principal principal) {
        return ResponseEntity.ok(propiedadService.getAll(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropiedadResponse> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(propiedadService.getById(id, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropiedadResponse> update(@PathVariable Long id, @Valid @RequestBody PropiedadRequest request,  Principal principal) {
        return ResponseEntity.ok(propiedadService.update(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        propiedadService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}