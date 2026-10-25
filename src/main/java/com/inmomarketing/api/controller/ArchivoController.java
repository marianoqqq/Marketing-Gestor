package com.inmomarketing.api.controller;

import com.inmomarketing.api.dto.response.ArchivoResponse;
import com.inmomarketing.api.model.ArchivoEntity;
import com.inmomarketing.api.service.ArchivoService;
import com.inmomarketing.api.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ArchivoController {
    private final ArchivoService archivoService;
    private final StorageService storageService;

    @PostMapping("/propiedades/{id}/fotos")
    public ResponseEntity<List<ArchivoResponse>> subirFotos(@PathVariable Long id,@RequestParam("fotos") List<MultipartFile> fotos,  Principal principal) {
        return new ResponseEntity<>(archivoService.subirFotos(id, fotos, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/propiedades/{id}/fotos")
    public ResponseEntity<List<ArchivoResponse>> getFotos(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(archivoService.getFotos(id, principal.getName()));
    }

    @GetMapping("/archivos/{id}")
    public ResponseEntity<Resource> verArchivo(@PathVariable Long id, Principal principal) throws Exception {
        ArchivoEntity archivo = archivoService.getArchivo(id, principal.getName());
        Resource recurso = storageService.obtenerRecurso(archivo.getRuta(), archivo.getContentType());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.getContentType()))
                .body(recurso);
    }
}