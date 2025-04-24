package com.ddorokhov.origin.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlValidatorTest {

    private UrlValidator urlValidator;

    @BeforeEach
    void init() {
        urlValidator = new UrlValidator();
    }

    @Test
    @DisplayName("Should return true for valid http url")
    void shouldReturnTrueForValidHttpUrl() {
        assertTrue(urlValidator.isValid("http://example.com", null));
    }

    @Test
    @DisplayName("Should return true for valid https url")
    void shouldReturnTrueForValidHttpsUrl() {
        assertTrue(urlValidator.isValid("https://example.com", null));
    }

    @Test
    @DisplayName("Should return false for invalid url")
    void shouldReturnFalseForInvalidUrl() {
        assertFalse(urlValidator.isValid("invalid url", null));
    }

    @Test
    @DisplayName("Should return false for invalid url. Http")
    void shouldReturnFalseForInvalidUrlHttp() {
        assertFalse(urlValidator.isValid("http:/example.com", null));
    }

    @Test
    @DisplayName("Should return false for invalid url. Https")
    void shouldReturnFalseForInvalidUrlHttps() {
        assertFalse(urlValidator.isValid("https:/example.com", null));
    }

    @Test
    @DisplayName("Should return false for missing protocol")
    void shouldReturnFalseForMissingProtocol() {
        assertFalse(urlValidator.isValid("www.example.com", null));
    }

    @Test
    @DisplayName("Should return false for null")
    void shouldReturnFalseForNullInput() {
        assertFalse(urlValidator.isValid(null, null));
    }

    @Test
    @DisplayName("Should return false for empty string")
    void shouldReturnFalseForEmptyInput() {
        assertFalse(urlValidator.isValid("", null));

    }
}