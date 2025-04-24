package com.ddorokhov.origin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(
        name = "url_entity",
        indexes = {
                @Index(name = "idx_original_url", columnList = "originalUrl")
        }
)
public class UrlEntity {
    @Id
    String shortenedUrl;
    @Column(unique = true)
    String originalUrl;
}
