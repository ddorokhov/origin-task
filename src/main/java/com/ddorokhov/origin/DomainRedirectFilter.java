package com.ddorokhov.origin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class DomainRedirectFilter extends OncePerRequestFilter {
    private final UrlService urlService;

    public DomainRedirectFilter(UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<UrlEntity> urlEntityOptional = urlService.getOriginal(request.getRequestURL().toString());
        if (urlEntityOptional.isPresent()){
            String originalUrl = urlEntityOptional.get().getOriginalUrl();
            response.setStatus(HttpServletResponse.SC_FOUND); // 302
            response.setHeader("Location", originalUrl);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
