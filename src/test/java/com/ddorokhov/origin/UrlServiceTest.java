package com.ddorokhov.origin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    private final String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";
    private final String shortenedUrl = "http://short.ly/a1B2c3";
    private final UrlEntity entity = new UrlEntity(shortenedUrl, originalUrl);

    @Test
    @DisplayName("Returns mapping if original URL exists")
    void returnsMappingIfOriginalExists() {
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(entity));

        Optional<UrlEntity> result = urlService.get(originalUrl);

        assertTrue(result.isPresent());
        assertEquals(shortenedUrl, result.get().getShortenedUrl());
    }

    @Test
    @DisplayName("Resolves shortened URL to original")
    void resolvesShortenedUrl() {
        when(urlRepository.findById(shortenedUrl)).thenReturn(Optional.of(entity));

        Optional<UrlEntity> result = urlService.getOriginal(shortenedUrl);

        assertTrue(result.isPresent());
        assertEquals(originalUrl, result.get().getOriginalUrl());
    }

    @Test
    @DisplayName("Returns existing mapping if already shortened")
    void returnsExistingMapping() {
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(entity));

        ResponseEntity<UrlEntity> response = urlService.createOrGet(originalUrl);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(entity, response.getBody());
        verify(urlRepository, never()).save(any());
    }

    @Test
    @DisplayName("Creates and saves new mapping when original URL is not found")
    void createsNewMappingIfNotExists() {
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.findById(any())).thenReturn(Optional.empty());
        when(urlRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<UrlEntity> response = urlService.createOrGet(originalUrl);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(originalUrl, response.getBody().getOriginalUrl());
        assertTrue(response.getHeaders().getLocation().toString().startsWith("http://short.ly/"));
    }

    @Test
    @DisplayName("Deletes mapping if original URL exists")
    void deletesExistingMapping() {
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(entity));

        urlService.delete(originalUrl);

        verify(urlRepository).delete(entity);
    }

    @Test
    @DisplayName("Skips deletion if no mapping found")
    void skipsDeletionIfMappingNotFound() {
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        urlService.delete(originalUrl);
        verify(urlRepository, never()).delete(any());
    }

    @Test
    @DisplayName("randomizeCase should return mixed-case alphanumeric string")
    void randomizeCaseProducesValidOutput() {
        String input = "abc123";
        String result = urlService.randomizeCase(input);

        assertEquals(input.length(), result.length());

        for (int i = 0; i < input.length(); i++) {
            char original = input.charAt(i);
            char actual = result.charAt(i);

            if (Character.isDigit(original)) {
                assertEquals(original, actual, "Digits should not be changed");
            }

            if (Character.isLetter(original)){
                assertEquals(Character.toLowerCase(original), Character.toLowerCase(actual), "Letter content should match (ignoring case)");
            }
        }

        for (char c : result.toCharArray()) {
            assertTrue(Character.isLetterOrDigit(c), "Should only contain letters and digits");
        }
    }
}
