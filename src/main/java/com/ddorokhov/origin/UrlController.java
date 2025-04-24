package com.ddorokhov.origin;

import com.ddorokhov.origin.validator.ValidUrl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ddorokhov.origin.Constants.PATH_ORIGINAL;
import static com.ddorokhov.origin.Constants.PATH_SHORTEN;

/**
 * REST controller for managing URL shortening operations.
 * <p>
 * Provides endpoints to shorten original URLs, retrieve shortened versions,
 * resolve shortened URLs back to original, and delete mappings.
 * </p>
 */
@RestController
@Validated
public class UrlController {
    private final UrlService urlService;
    /**
     * Constructs a controller with the required URL service.
     *
     * @param urlService service handling URL mapping logic
     */
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Retrieves a shortened URL for the given original URL.
     *
     * @param originalUrl the original URL to look up (must be valid and non-blank)
     * @return {@code 200 OK} with {@link UrlEntity} if found, or {@code 404 Not Found}
     */
    @GetMapping(path = PATH_SHORTEN)
    public ResponseEntity<UrlEntity> getShorten(@NotBlank @ValidUrl @RequestParam String originalUrl) {
        return ResponseEntity.of(urlService.get(originalUrl));
    }

    /**
     * Creates a shortened URL for the given original URL, or returns an existing one.
     *
     * @param originalUrl the original URL to shorten (must be valid and non-blank)
     * @return {@code 201 Created} if new, or {@code 200 OK} if already exists
     */
    @PostMapping(path = PATH_SHORTEN)
    public ResponseEntity<UrlEntity> createShorten(@NotBlank  @ValidUrl @RequestParam String originalUrl) {

        return urlService.createOrGet(originalUrl);
    }

    /**
     * Deletes an existing URL mapping by original URL.
     *
     * @param originalUrl the original URL to remove (must be valid and non-blank)
     */
    @DeleteMapping(path = PATH_SHORTEN)
    public void deleteShorten(@NotBlank @ValidUrl @RequestParam String originalUrl)  {
        urlService.delete(originalUrl);
    }

    /**
     * Resolves a shortened URL to its original form.
     *
     * @param shortenedUrl the shortened URL to resolve (must be valid and non-blank)
     * @return {@code 200 OK} with {@link UrlEntity} if found, or {@code 404 Not Found}
     */
    @GetMapping(path = PATH_ORIGINAL)
    public ResponseEntity<UrlEntity> getOriginal(@NotBlank  @ValidUrl @RequestParam String shortenedUrl)  {
        return ResponseEntity.of(urlService.getOriginal(shortenedUrl));
    }

    /**
     * Handles validation errors caused by constraint violations in request parameters.
     *
     * @param e the {@link ConstraintViolationException} with validation details
     * @return {@code 400 Bad Request} with a message describing the violation(s)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleInvalidUrl(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder("Invalid input:\n");
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            message
                    .append(violation.getInvalidValue())
                    .append(" <— ")
                    .append(violation.getMessage())
                    .append("\n");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message.toString());
    }
}



