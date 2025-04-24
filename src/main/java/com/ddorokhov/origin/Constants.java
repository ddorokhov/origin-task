package com.ddorokhov.origin;


import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String PATH_SHORTEN = "/shorten";
    public static final String DOMAIN_SHORT = "short.ly";
    public static final String PATH_ORIGINAL = "/original";
    public static final String ERROR_MESSAGE_SHORT_URL_EXISTS = "Entry with the same shortened URL already exists";
    public static final String HTTP_PROTOCOL_DOMAIN_SHORT = "http://" + DOMAIN_SHORT+"/";
}
