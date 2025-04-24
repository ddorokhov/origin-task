package com.ddorokhov.origin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, String> {
    Optional<UrlEntity> findByOriginalUrl(String originalUrl);
}
