package com.inmomarketing.api;

import com.inmomarketing.api.model.ArchivoEntity;
import com.inmomarketing.api.repository.ArchivoRepository;
import com.inmomarketing.api.service.StorageService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.Assumptions;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ArchivoFlowWithR2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ArchivoRepository archivoRepository;

    private final List<String> rutasParaLimpiar = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (String ruta : rutasParaLimpiar) {
            try {
                storageService.eliminarArchivo(ruta);
                System.out.println("Limpieza R2 completada para: " + ruta);
            } catch (Exception e) {
                System.err.println("No se pudo eliminar " + ruta + ": " + e.getMessage());
            }
        }
    }

    private String loginToken(String email, String inmo) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("""
                                {"nombre":"Agente R2","email":"%s","password":"secret123","nombreInmobiliaria":"%s"}"""
                                .formatted(email, inmo)))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(post("/auth")
                        .contentType("application/json")
                        .content("""
                                {"email":"%s","password":"secret123"}""".formatted(email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.accessToken");
    }

    @Test
    void testSubidaYDescargaDeFotoConCloudflareR2() throws Exception {
        Assumptions.assumeTrue(storageService.isR2Configurado(), "Cloudflare R2 no está configurado, omitiendo test de integración R2 en vivo");
        String token = loginToken("r2user_" + System.currentTimeMillis() + "@test.com", "Inmo R2");

        // 1. Crear propiedad
        String propBody = mockMvc.perform(post("/api/propiedades")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                    "titulo": "Casa quinta en Pilar",
                                    "tipo": "CASA",
                                    "operacion": "VENTA",
                                    "precio": 180000,
                                    "direccion": "Ruta 8 Km 50",
                                    "ambientes": 4,
                                    "metrosCuadrados": 200,
                                    "descripcion": "Hermosa casa con parque y pileta"
                                }"""))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer propiedadId = JsonPath.read(propBody, "$.id");

        // 2. Subir imagen binaria a través de la API
        byte[] imagenFake = "CONTENIDO_BINARIO_IMAGEN_TEST_JPEG".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "fotos",
                "fachada_quinta.jpg",
                "image/jpeg",
                imagenFake
        );

        String uploadResponse = mockMvc.perform(multipart("/api/propiedades/" + propiedadId + "/fotos")
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].nombreOriginal").value("fachada_quinta.jpg"))
                .andExpect(jsonPath("$[0].contentType").value("image/jpeg"))
                .andReturn().getResponse().getContentAsString();

        Integer archivoId = JsonPath.read(uploadResponse, "$[0].id");

        // Verificar que la ruta guardada en la base de datos pertenece a R2 (comienza con "fotos/")
        ArchivoEntity archivoEntity = archivoRepository.findById(archivoId.longValue()).orElseThrow();
        rutasParaLimpiar.add(archivoEntity.getRuta());
        assertTrue(archivoEntity.getRuta().startsWith("fotos/"), "La ruta debería ser una key de R2 comenzando con 'fotos/'");

        // 3. Obtener fotos de la propiedad
        mockMvc.perform(get("/api/propiedades/" + propiedadId + "/fotos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(archivoId))
                .andExpect(jsonPath("$[0].nombreOriginal").value("fachada_quinta.jpg"));

        // 4. Descargar archivo binario vía /api/archivos/{id} (descarga desde Cloudflare R2)
        byte[] descargado = mockMvc.perform(get("/api/archivos/" + archivoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        assertArrayEquals(imagenFake, descargado, "El contenido descargado de R2 debe coincidir exactamente con el subido");
    }
}
