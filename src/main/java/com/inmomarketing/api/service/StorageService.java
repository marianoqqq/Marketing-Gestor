package com.inmomarketing.api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String guardarArchivo(String subcarpeta, String nombreArchivo, MultipartFile archivo) throws IOException;

    String guardarBytes(String subcarpeta, String nombreArchivo, String contentType, byte[] contenido) throws IOException;

    Resource obtenerRecurso(String ruta, String contentType) throws IOException;

    byte[] obtenerBytes(String ruta) throws IOException;

    void eliminarArchivo(String ruta);

    String obtenerUrlPublica(String ruta);

    boolean isR2Configurado();
}
