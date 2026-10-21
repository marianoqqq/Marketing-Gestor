package com.inmomarketing.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublicacionFlowTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ChatModel chatModel;

	private void registrar(String email, String nombreInmo) throws Exception {
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Test","email":"%s","password":"secret123","nombreInmobiliaria":"%s"}"""
								.formatted(email, nombreInmo)))
				.andExpect(status().isCreated());
	}

	private String loginToken(String email) throws Exception {
		String body = mockMvc.perform(post("/auth")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"%s","password":"secret123"}""".formatted(email)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(body, "$.accessToken");
	}

	@Test
	void flujoCompletoPublicaciones() throws Exception {
		when(chatModel.call(anyString())).thenReturn("¡Gran Oportunidad! 2 Ambientes Luminoso");

		// 1. Registro y login
		registrar("agente1@test.com", "Inmo Norte");
		String token1 = loginToken("agente1@test.com");

		// 2. Crear propiedad
		String propBody = mockMvc.perform(post("/api/propiedades")
						.header("Authorization", "Bearer " + token1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"titulo": "Depto 2 amb centro",
									"tipo": "DEPARTAMENTO",
									"operacion": "VENTA",
									"precio": 75000,
									"direccion": "Av. Corrientes 1200",
									"ambientes": 2,
									"metrosCuadrados": 45.5,
									"descripcion": "Muy luminoso con balcón"
								}"""))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		Integer propiedadId = JsonPath.read(propBody, "$.id");

		// 3. Crear publicación -> 201 y estado BORRADOR
		String pubBody = mockMvc.perform(post("/api/publicaciones")
						.header("Authorization", "Bearer " + token1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"propiedadId": %d,
									"red": "INSTAGRAM",
									"formato": "IMAGEN",
									"indicaciones": "tono juvenil y fresco",
									"estiloImagen": "Flyer comercial moderno"
								}""".formatted(propiedadId)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.estado").value("BORRADOR"))
				.andExpect(jsonPath("$.red").value("INSTAGRAM"))
				.andExpect(jsonPath("$.formato").value("IMAGEN"))
				.andExpect(jsonPath("$.estiloImagen").value("Flyer comercial moderno"))
				.andReturn().getResponse().getContentAsString();

		Integer publicacionId = JsonPath.read(pubBody, "$.id");

		// 4. Probar botón "Sugerir texto para la imagen" con IA
		mockMvc.perform(post("/api/publicaciones/%d/sugerir-texto-imagen".formatted(publicacionId))
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.textoSugerido").isNotEmpty());

		// 5. El usuario edita el texto de la imagen si lo desea
		mockMvc.perform(patch("/api/publicaciones/%d/texto-imagen".formatted(publicacionId))
						.header("Authorization", "Bearer " + token1)
						.param("textoImagen", "¡OPORTUNIDAD ÚNICA! 2 Ambientes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.textoImagen").value("¡OPORTUNIDAD ÚNICA! 2 Ambientes"));

		// 6. Intentar marcar como publicada antes de generar -> 400 Bad Request
		mockMvc.perform(post("/api/publicaciones/%d/publicada".formatted(publicacionId))
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isBadRequest());

		// 7. Generar texto del post con IA -> 200 y estado LISTA
		when(chatModel.call(anyString())).thenReturn("¡Hermoso departamento en venta! 🏡✨ #Inmuebles #Oportunidad");

		mockMvc.perform(post("/api/publicaciones/%d/generar".formatted(publicacionId))
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("LISTA"))
				.andExpect(jsonPath("$.texto").isNotEmpty());

		// 8. Marcar como publicada -> 200 y estado PUBLICADA con fecha
		mockMvc.perform(post("/api/publicaciones/%d/publicada".formatted(publicacionId))
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("PUBLICADA"))
				.andExpect(jsonPath("$.publicadaEn").isNotEmpty());

		// 9. Otro usuario de otra inmobiliaria no puede acceder -> 404
		registrar("agente2@test.com", "Inmo Sur");
		String token2 = loginToken("agente2@test.com");

		mockMvc.perform(get("/api/publicaciones/%d".formatted(publicacionId))
						.header("Authorization", "Bearer " + token2))
				.andExpect(status().isNotFound());

		// 10. Eliminar publicación -> 204
		mockMvc.perform(delete("/api/publicaciones/%d".formatted(publicacionId))
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isNoContent());

		// Verificar que ya no existe -> 404
		mockMvc.perform(get("/api/publicaciones/%d".formatted(publicacionId))
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isNotFound());
	}
}
