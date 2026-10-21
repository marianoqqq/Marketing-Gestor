package com.inmomarketing.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmomarketing.api.model.ArchivoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OpenAiImageService {

    private static final String URL = "https://api.openai.com/v1/images/edits";

    private final ObjectMapper objectMapper;

    @Value("${openai.image.api-key:}")
    private String apiKey;

    @Value("${openai.image.model:gpt-image-2.5-sunburst}")
    private String modelo;

    public byte[] generarImagen(List<ArchivoEntity> fotos, String prompt) throws Exception {
        MultipartBodyBuilder body = new MultipartBodyBuilder();
        body.part("model", modelo);
        body.part("prompt", prompt);
        body.part("size", "1024x1024");
        for (ArchivoEntity foto : fotos) {
            body.part("image[]", new FileSystemResource(foto.getRuta()))
                    .filename(foto.getNombreOriginal())
                    .contentType(MediaType.parseMediaType(foto.getContentType()));
        }

        String respuesta = RestClient.create().post()
                .uri(URL)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body.build())
                .retrieve()
                .body(String.class);

        String base64 = objectMapper.readTree(respuesta).path("data").path(0).path("b64_json").asText();
        return Base64.getDecoder().decode(base64);
    }
}
