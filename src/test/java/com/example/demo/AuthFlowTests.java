package com.example.demo;

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

	@Test
	void registerLoginAndAccessProtectedEndpoint() throws Exception {
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"admin@test.com","password":"secret123","role":"ROLE_ADMIN"}"""))
				.andExpect(status().isCreated());

		String body = mockMvc.perform(post("/auth")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"admin@test.com","password":"secret123"}"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty())
				.andReturn().getResponse().getContentAsString();
		String accessToken = JsonPath.read(body, "$.accessToken");

		mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk());
	}

	@Test
	void protectedEndpointWithoutTokenReturns401() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void loginWithWrongPasswordReturns401() throws Exception {
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"user@test.com","password":"secret123","role":"ROLE_USER"}"""))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/auth")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"user@test.com","password":"wrong"}"""))
				.andExpect(status().isUnauthorized());
	}

}
