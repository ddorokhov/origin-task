package com.ddorokhov.origin.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URL;

/**
 * Validates that a given String is a well-formed URL.
 */
public class UrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if (!value.contains("http://") && !value.contains("https://")) return false;
            new URL(value).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}