package com.inmomarketing.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTests {

	@Autowired
	private MockMvc mockMvc;

	private void registrar(String email) throws Exception {
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Ana","email":"%s","password":"secret123","nombreInmobiliaria":"Inmo Test"}"""
								.formatted(email)))
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
	void registroCreaInmobiliariaYPermiteUsarLaApi() throws Exception {
		registrar("ana@test.com");
		String token = loginToken("ana@test.com");

		mockMvc.perform(get("/api/inmobiliaria").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Inmo Test"));

		mockMvc.perform(get("/api/propiedades").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void usuarioNoPuedeEntrarAEndpointsDeAdmin() throws Exception {
		registrar("user@test.com");
		String token = loginToken("user@test.com");

		mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden());
	}

	@Test
	void sinTokenDevuelve401() throws Exception {
		mockMvc.perform(get("/api/propiedades"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void passwordIncorrectaDevuelve401() throws Exception {
		registrar("mal@test.com");
		mockMvc.perform(post("/auth")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"mal@test.com","password":"incorrecta"}"""))
				.andExpect(status().isUnauthorized());
	}
}