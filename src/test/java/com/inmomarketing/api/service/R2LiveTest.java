package com.inmomarketing.api.service;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class R2LiveTest {

    @Test
    void testConexionR2Real() throws Exception {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application-local.properties")) {
            if (is == null) {
                System.out.println("application-local.properties no encontrado en classpath de test, buscando en filesystem...");
                java.io.File localFile = new java.io.File("src/main/resources/application-local.properties");
                if (localFile.exists()) {
                    try (var fis = new java.io.FileInputStream(localFile)) {
                        props.load(fis);
                    }
                }
            } else {
                props.load(is);
            }
        }

        String endpoint = props.getProperty("cloudflare.r2.endpoint");
        String accountId = props.getProperty("cloudflare.r2.account-id");
        String accessKey = props.getProperty("cloudflare.r2.access-key");
        String secretKey = props.getProperty("cloudflare.r2.secret-key");
        String bucketName = props.getProperty("cloudflare.r2.bucket-name");
        String publicUrl = props.getProperty("cloudflare.r2.public-url", "");

        Assumptions.assumeTrue(bucketName != null && !bucketName.isBlank()
                && accessKey != null && !accessKey.isBlank()
                && secretKey != null && !secretKey.isBlank(),
                "Credenciales de Cloudflare R2 no presentes en application-local.properties, omitiendo test en vivo.");

        System.out.println("Endpoint: " + endpoint);
        System.out.println("Bucket: " + bucketName);
        System.out.println("AccessKey: " + accessKey.substring(0, Math.min(6, accessKey.length())) + "...");

        StorageServiceImpl storageService = new StorageServiceImpl();
        ReflectionTestUtils.setField(storageService, "endpoint", endpoint);
        ReflectionTestUtils.setField(storageService, "accountId", accountId);
        ReflectionTestUtils.setField(storageService, "accessKey", accessKey);
        ReflectionTestUtils.setField(storageService, "secretKey", secretKey);
        ReflectionTestUtils.setField(storageService, "bucketName", bucketName);
        ReflectionTestUtils.setField(storageService, "publicUrl", publicUrl);

        storageService.init();

        boolean r2Enabled = (boolean) ReflectionTestUtils.getField(storageService, "r2Enabled");
        assertTrue(r2Enabled, "R2 debe haber quedado habilitado");

        String nombreArchivo = "test_connectivity_" + System.currentTimeMillis() + ".txt";
        byte[] contenido = "Probando subida a Cloudflare R2 desde InmoMarketing API".getBytes(StandardCharsets.UTF_8);

        // 1. Guardar archivo
        String key = storageService.guardarBytes("tests", nombreArchivo, "text/plain", contenido);
        System.out.println("Archivo guardado con key: " + key);
        assertNotNull(key);
        assertEquals("tests/" + nombreArchivo, key);

        // 2. Obtener bytes y comparar
        byte[] descargado = storageService.obtenerBytes(key);
        String textoDescargado = new String(descargado, StandardCharsets.UTF_8);
        System.out.println("Contenido recuperado: " + textoDescargado);
        assertEquals(new String(contenido, StandardCharsets.UTF_8), textoDescargado);

        // 3. Eliminar archivo
        storageService.eliminarArchivo(key);
        System.out.println("Archivo eliminado exitosamente de R2.");
    }
}
