package com.ddorokhov.origin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

class DomainRedirectFilterTest {

    private UrlService urlService;
    private DomainRedirectFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void init() {
        urlService = mock(UrlService.class);
        filter = new DomainRedirectFilter(urlService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Should redirect if shorten url found")
    void originalFound() throws ServletException, IOException {
        String shortUrl = "http://short.ly/a1B2c3";
        String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";

        when(request.getRequestURL()).thenReturn(new StringBuffer(shortUrl));
        when(urlService.getOriginal(shortUrl)).thenReturn(Optional.of(new UrlEntity(shortUrl, originalUrl)));

        filter.doFilterInternal(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FOUND);
        verify(response).setHeader("Location", originalUrl);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should continue filter chain when shorten url not found")
    void originalNotFound() throws ServletException, IOException {
        String shortUrl = "http://short.ly/doesnotexist";

        when(request.getRequestURL()).thenReturn(new StringBuffer(shortUrl));
        when(urlService.getOriginal(shortUrl)).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_FOUND);
        verify(response, never()).setHeader(eq("Location"), anyString());
    }
}