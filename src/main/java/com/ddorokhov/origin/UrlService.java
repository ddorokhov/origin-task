package com.ddorokhov.origin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.ddorokhov.origin.Constants.ERROR_MESSAGE_SHORT_URL_EXISTS;
import static com.ddorokhov.origin.Constants.HTTP_PROTOCOL_DOMAIN_SHORT;

/**
 * Service responsible for URL shortening operations.
 * <p>
 * Supports resolving, caching, generating, and deleting shortened URLs.
 */
@Service
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * Retrieves an existing shortened URL by original URL.
     *
     * @param originalUrl the original URL
     * @return optional containing the URL mapping if found
     */
    @Cacheable(value = "urlEntity", key = "#originalUrl")
    public Optional<UrlEntity> get(String originalUrl){
        return urlRepository.findByOriginalUrl(originalUrl);
    }

    /**
     * Retrieves the original URL by shortened URL.
     *
     * @param shortenedUrl the shortened URL
     * @return optional containing the URL mapping if found
     */
    @Cacheable(value = "urlEntity", key = "#shortenedUrl")
    public Optional<UrlEntity> getOriginal(String shortenedUrl){
        return urlRepository.findById(shortenedUrl);
    }


    /**
     * Creates a shortened URL if it does not exist yet,
     * or returns the existing mapping.
     *
     * @param originalUrl the original URL to shorten
     * @return 201 if new, or 200 if already exists
     */
    @Transactional
    public ResponseEntity<UrlEntity> createOrGet(String originalUrl){
        Optional<UrlEntity> urlEntityOptional = urlRepository.findByOriginalUrl(originalUrl);
        if (urlEntityOptional.isPresent()) return ResponseEntity.ok().body(urlEntityOptional.get());

        String shortenedUrl;

        while(true){
            shortenedUrl = HTTP_PROTOCOL_DOMAIN_SHORT + generateShortenedUrl();
            Optional<UrlEntity> existingEntryOptional = urlRepository.findById(shortenedUrl);
            if (existingEntryOptional.isEmpty()) break;
            log.warn("{}: {}", ERROR_MESSAGE_SHORT_URL_EXISTS, shortenedUrl);
        }

        UrlEntity result = urlRepository.save(new UrlEntity(shortenedUrl,originalUrl));
        saveToCache(result);

        URI location = URI.create(result.getShortenedUrl());

        return ResponseEntity.created(location).body(result);
    }


    /**
     * Deletes a mapping by original URL and evicts both cache entries.
     *
     * @param originalUrl the original URL
     */
    @CacheEvict(value = "urlEntity", key = "#originalUrl")
    @Transactional
    public void delete(String originalUrl){
        Optional<UrlEntity> urlEntityOptional = urlRepository.findByOriginalUrl(originalUrl);
        if (urlEntityOptional.isEmpty()) return;

        UrlEntity urlEntity = urlEntityOptional.get();

        urlRepository.delete(urlEntity);
        evictShortened(urlEntity.getShortenedUrl());

    }



    /**
     * Updates the cache with the newly saved entity (keyed by original URL).
     *
     * @param result the saved entity
     * @return the same entity
     */
    @CachePut(value = "urlEntity", key = "#result.originalUrl")
    public UrlEntity saveToCache(UrlEntity result) {
        return result;
    }

    /**
     * Evicts the cache entry by shortened URL.
     *
     * @param shortenedUrl the shortened URL
     */
    @CacheEvict(value = "urlEntity", key = "#shortenedUrl")
    public void evictShortened(String shortenedUrl) {
    }

    /**
     * Generates a 6-character case-randomized alphanumeric string based on UUID.
     *
     * @return randomized shortened ID
     */
    protected String generateShortenedUrl(){
        return randomizeCase(UUID.randomUUID().toString().replace("-","").substring(0,6));
    }

    /**
     * Randomizes the case of each alphabetic character in the input string.
     *
     * @param input the base string
     * @return a string with randomly upper/lowercased letters
     */
    protected String randomizeCase(String input) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                if (random.nextBoolean()) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(Character.toLowerCase(c));
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
