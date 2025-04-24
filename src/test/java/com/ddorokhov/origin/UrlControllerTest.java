package com.ddorokhov.origin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static com.ddorokhov.origin.Constants.PATH_ORIGINAL;
import static com.ddorokhov.origin.Constants.PATH_SHORTEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    private final String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";
    private final String shortenedUrl = "http://short.ly/a1B2c3";
    private final UrlEntity entity = new UrlEntity(shortenedUrl, originalUrl);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Returns shortened URL if found (200 OK)")
    void shortenFound() throws Exception {
        when(urlService.get(originalUrl)).thenReturn(Optional.of(entity));

        MvcResult result = mockMvc.perform(get(PATH_SHORTEN).param("originalUrl", originalUrl))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        UrlEntity responseEntity = objectMapper.readValue(responseBody, UrlEntity.class);

        assertEquals(originalUrl, responseEntity.getOriginalUrl());
        assertEquals(shortenedUrl, responseEntity.getShortenedUrl());
    }

    @Test
    @DisplayName("Returns 404 when original URL is not found")
    void shortenNotFound() throws Exception {
        when(urlService.get(originalUrl)).thenReturn(Optional.empty());

        mockMvc.perform(get(PATH_SHORTEN).param("originalUrl", originalUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Creates shortened URL if not already mapped (201 Created)")
    void shortenCreated() throws Exception {
        when(urlService.createOrGet(originalUrl))
                .thenReturn(ResponseEntity.created(null).body(entity));

        MvcResult result = mockMvc.perform(post(PATH_SHORTEN).param("originalUrl", originalUrl))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        UrlEntity responseEntity = objectMapper.readValue(json, UrlEntity.class);

        assertEquals(originalUrl, responseEntity.getOriginalUrl());
        assertEquals(shortenedUrl, responseEntity.getShortenedUrl());
    }

    @Test
    @DisplayName("Deletes mapping and responds with 200 OK")
    void shortenDeleted() throws Exception {
        doNothing().when(urlService).delete(originalUrl);

        mockMvc.perform(delete(PATH_SHORTEN).param("originalUrl", originalUrl))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Resolves shortened URL to original (200 OK)")
    void originalFound() throws Exception {
        when(urlService.getOriginal(shortenedUrl)).thenReturn(Optional.of(entity));

        MvcResult result = mockMvc.perform(get(PATH_ORIGINAL).param("shortenedUrl", shortenedUrl))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        UrlEntity responseEntity = objectMapper.readValue(responseJson, UrlEntity.class);

        assertEquals(originalUrl, responseEntity.getOriginalUrl());
        assertEquals(shortenedUrl, responseEntity.getShortenedUrl());
    }

    @Test
    @DisplayName("Returns 404 when shortened URL does not exist")
    void originalNotFound() throws Exception {
        when(urlService.getOriginal(shortenedUrl)).thenReturn(Optional.empty());

        mockMvc.perform(get(PATH_ORIGINAL).param("shortenedUrl", shortenedUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns 400 for invalid URL input")
    void createShortenWithInvalidUrl() throws Exception {
        MvcResult result = mockMvc.perform(post(PATH_SHORTEN).param("originalUrl", "invalid-url"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        Assertions.assertTrue(responseBody.contains("Invalid"));
    }
}