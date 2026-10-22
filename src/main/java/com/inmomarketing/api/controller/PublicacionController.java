package com.inmomarketing.api.controller;

import com.inmomarketing.api.dto.request.PublicacionRequest;
import com.inmomarketing.api.dto.response.PublicacionResponse;
import com.inmomarketing.api.dto.response.SugerenciaTextoResponse;
import com.inmomarketing.api.service.PublicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PublicacionController {

    private final PublicacionService publicacionService;

    @PostMapping
    public ResponseEntity<PublicacionResponse> create(@Valid @RequestBody PublicacionRequest request, Principal principal) {
        return new ResponseEntity<>(publicacionService.create(request, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PublicacionResponse>> getAll(Principal principal) {
        return ResponseEntity.ok(publicacionService.getAll(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacionResponse> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(publicacionService.getById(id, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        publicacionService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/generar")
    public ResponseEntity<PublicacionResponse> generar(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(publicacionService.generarTexto(id, principal.getName()));
    }

    @PostMapping("/{id}/publicada")
    public ResponseEntity<PublicacionResponse> marcarPublicada(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(publicacionService.marcarPublicada(id, principal.getName()));
    }

    @PostMapping("/{id}/generar-imagen")
    public ResponseEntity<PublicacionResponse> generarImagen(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(publicacionService.generarImagen(id, principal.getName()));
    }

    @PostMapping("/{id}/sugerir-texto-imagen")
    public ResponseEntity<SugerenciaTextoResponse> sugerirTextoImagen(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(publicacionService.sugerirTextoImagen(id, principal.getName()));
    }

    @PatchMapping("/{id}/texto-imagen")
    public ResponseEntity<PublicacionResponse> actualizarTextoImagen(
            @PathVariable Long id,
            @RequestParam(required = false) String textoImagen,
            @RequestParam(required = false) String estiloImagen,
            Principal principal) {
        return ResponseEntity.ok(publicacionService.actualizarTextoImagen(id, textoImagen, estiloImagen, principal.getName()));
    }
}
