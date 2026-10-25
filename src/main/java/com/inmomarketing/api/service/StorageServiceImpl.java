package com.inmomarketing.api.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    @Value("${cloudflare.r2.account-id:}")
    private String accountId;

    @Value("${cloudflare.r2.access-key:}")
    private String accessKey;

    @Value("${cloudflare.r2.secret-key:}")
    private String secretKey;

    @Value("${cloudflare.r2.bucket-name:}")
    private String bucketName;

    @Value("${cloudflare.r2.endpoint:}")
    private String endpoint;

    @Value("${cloudflare.r2.public-url:}")
    private String publicUrl;

    private S3Client s3Client;
    private boolean r2Enabled = false;

    @PostConstruct
    public void init() {
        if (bucketName != null && !bucketName.isBlank()
                && accessKey != null && !accessKey.isBlank()
                && secretKey != null && !secretKey.isBlank()) {
            try {
                String s3Endpoint = (endpoint != null && !endpoint.isBlank())
                        ? endpoint
                        : (accountId != null && !accountId.isBlank()
                            ? "https://" + accountId + ".r2.cloudflarestorage.com"
                            : null);

                if (s3Endpoint == null) {
                    log.warn("Cloudflare R2 no tiene configurado ni endpoint ni account-id. Usando almacenamiento local.");
                    this.r2Enabled = false;
                    return;
                }

                log.info("Inicializando cliente S3 para Cloudflare R2 con endpoint: {}", s3Endpoint);

                this.s3Client = S3Client.builder()
                        .endpointOverride(URI.create(s3Endpoint))
                        .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        ))
                        .region(Region.of("auto"))
                        .serviceConfiguration(S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build())
                        .build();

                this.r2Enabled = true;
                log.info("Cloudflare R2 configurado exitosamente para el bucket: {}", bucketName);
            } catch (Exception e) {
                log.error("Error al inicializar Cloudflare R2: {}. Se utilizará almacenamiento local.", e.getMessage(), e);
                this.r2Enabled = false;
            }
        } else {
            log.info("Cloudflare R2 no está configurado (faltan credenciales). Utilizando almacenamiento local en uploads/");
            this.r2Enabled = false;
        }
    }

    @Override
    public String guardarArchivo(String subcarpeta, String nombreArchivo, MultipartFile archivo) throws IOException {
        String contentType = archivo.getContentType() != null ? archivo.getContentType() : "application/octet-stream";
        byte[] bytes = archivo.getBytes();
        return guardarBytes(subcarpeta, nombreArchivo, contentType, bytes);
    }

    @Override
    public String guardarBytes(String subcarpeta, String nombreArchivo, String contentType, byte[] contenido) throws IOException {
        if (r2Enabled) {
            String key = normalizarKey(subcarpeta, nombreArchivo);
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(contenido));
            log.info("Archivo subido a Cloudflare R2: bucket={}, key={}", bucketName, key);
            return key;
        } else {
            String carpeta = "uploads/" + subcarpeta + "/";
            File destino = new File(carpeta, nombreArchivo).getAbsoluteFile();
            destino.getParentFile().mkdirs();
            Files.write(destino.toPath(), contenido);
            log.info("Archivo guardado en almacenamiento local: {}", destino.getPath());
            return carpeta + nombreArchivo;
        }
    }

    @Override
    public Resource obtenerRecurso(String ruta, String contentType) throws IOException {
        byte[] bytes = obtenerBytes(ruta);
        return new ByteArrayResource(bytes);
    }

    @Override
    public byte[] obtenerBytes(String ruta) throws IOException {
        if (esRutaLocal(ruta)) {
            Path path = Paths.get(ruta).toAbsolutePath();
            if (!Files.exists(path)) {
                throw new IOException("Archivo local no encontrado: " + ruta);
            }
            return Files.readAllBytes(path);
        }

        if (r2Enabled) {
            String key = limpiarKey(ruta);
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getRequest);
            return responseBytes.asByteArray();
        }

        // Si no está habilitado R2 y la ruta no parece local, intentamos leerla como ruta del filesystem
        Path path = Paths.get(ruta).toAbsolutePath();
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        }
        throw new IOException("No se pudo obtener el archivo: " + ruta);
    }

    @Override
    public void eliminarArchivo(String ruta) {
        if (esRutaLocal(ruta)) {
            try {
                Files.deleteIfExists(Paths.get(ruta));
                log.info("Archivo local eliminado: {}", ruta);
            } catch (IOException e) {
                log.warn("No se pudo eliminar el archivo local: {}", ruta);
            }
            return;
        }

        if (r2Enabled) {
            try {
                String key = limpiarKey(ruta);
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();
                s3Client.deleteObject(deleteRequest);
                log.info("Archivo eliminado de Cloudflare R2: key={}", key);
            } catch (Exception e) {
                log.warn("Error al eliminar archivo de Cloudflare R2: key={}, error={}", ruta, e.getMessage());
            }
        }
    }

    @Override
    public String obtenerUrlPublica(String ruta) {
        if (publicUrl != null && !publicUrl.isBlank() && !esRutaLocal(ruta)) {
            String baseUrl = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
            return baseUrl + "/" + limpiarKey(ruta);
        }
        return null;
    }

    private boolean esRutaLocal(String ruta) {
        return ruta != null && (ruta.startsWith("uploads/") || ruta.startsWith("uploads\\") || ruta.startsWith("./uploads"));
    }

    private String normalizarKey(String subcarpeta, String nombreArchivo) {
        String sub = subcarpeta.endsWith("/") ? subcarpeta : subcarpeta + "/";
        return sub + nombreArchivo;
    }

    private String limpiarKey(String ruta) {
        if (ruta == null) return "";
        if (ruta.startsWith("/")) {
            return ruta.substring(1);
        }
        return ruta;
    }
}
